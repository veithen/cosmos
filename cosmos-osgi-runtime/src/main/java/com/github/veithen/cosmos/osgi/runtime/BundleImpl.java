/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2018 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.cosmos.osgi.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.Attributes.Name;
import java.util.jar.JarInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BundleImpl extends AbstractBundle implements BundleRevision, BundleStartLevel {
    private static final Logger logger = LoggerFactory.getLogger(BundleImpl.class);

    static abstract class Reason<T> {
        abstract String format(T context);
    }
    
    private static final Reason<BundleImpl> USED_BY_BUNDLE = new Reason<BundleImpl>() {
        @Override
        String format(BundleImpl context) {
            return "it is used by bundle " + context.symbolicName;
        }
    };
    
    private static final Reason<String> CLASS_LOADING_REQUEST = new Reason<String> () {
        @Override
        String format(String context) {
            return "request to load class " + context;
        }
    };
    
    private static final AtomicInteger nextStartOrder = new AtomicInteger();
    
    private final Map<String,List<BundleCapability>> bundleCapabilities = new HashMap<>();
    private final BundleManager bundleManager;
    private final long id;
    private final String symbolicName;
    private final Attributes attrs;
    private final URL rootUrl;
    private final URL locationUrl;
    private final Version version;
    private final BundleWiringImpl wiring = new BundleWiringImpl();
    private BundleContextFactory bundleContextFactory;
    private BundleState state;
    private BundleActivator activator;
    private int startOrder = -1;

    private final Lazy<Set<String>> entries = new Lazy<Set<String>>() {
        @Override
        Set<String> initialize() {
            Set<String> entries = new HashSet<>();
            try (JarInputStream in = new JarInputStream(locationUrl.openStream())) {
                JarEntry entry;
                while ((entry = in.getNextJarEntry()) != null) {
                    entries.add(entry.getName());
                }
                return entries;
            } catch (IOException ex) {
                logger.warn(String.format("Failed to read entries for bundle %s", symbolicName), ex);
                return Collections.emptySet();
            }
        }
    };

    private final Lazy<ResourceBundle> resourceBundle = new Lazy<ResourceBundle>() {
        @Override
        ResourceBundle initialize() {
            try {
                String localization = getHeaderValue(Constants.BUNDLE_LOCALIZATION);
                if (localization == null) {
                    localization = Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
                }
                URL entry = getEntry(localization + ".properties");
                if (entry == null) {
                    return null;
                }
                try (InputStream in = entry.openStream()) {
                    return new PropertyResourceBundle(in);
                }
            } catch (BundleException | IOException ex) {
                logger.warn(String.format("Failed to load bundle localization for bundle %s", symbolicName), ex);
                return null;
            }
        }
    };

    public BundleImpl(BundleManager bundleManager, long id, String symbolicName, Attributes attrs, URL rootUrl) throws BundleException {
        this.bundleManager = bundleManager;
        this.id = id;
        this.symbolicName = symbolicName;
        this.attrs = attrs;
        this.rootUrl = rootUrl;
        if (rootUrl.getProtocol().equals("jar")) {
            String path = rootUrl.getPath();
            try {
                locationUrl = new URL(path.substring(0, path.lastIndexOf('!')));
            } catch (MalformedURLException ex) {
                throw new BundleException("Failed to extract bundle URL", ex);
            }
        } else {
            locationUrl = rootUrl;
        }
        version = Version.parseVersion(attrs.getValue(Constants.BUNDLE_VERSION));
        Map<String, Object> identityAttributes = new HashMap<>();
        identityAttributes.put(IdentityNamespace.IDENTITY_NAMESPACE, symbolicName);
        identityAttributes.put(IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE, version);
        identityAttributes.put(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE, IdentityNamespace.TYPE_BUNDLE);
        bundleCapabilities.put(IdentityNamespace.IDENTITY_NAMESPACE, Collections.<BundleCapability>singletonList(new BundleCapabilityImpl(this, IdentityNamespace.IDENTITY_NAMESPACE, new HashMap<String,String>(), identityAttributes)));
    }

    void initialize(BundleContextFactory bundleContextFactory) throws BundleException {
        this.bundleContextFactory = bundleContextFactory;
        if ("lazy".equals(getHeaderValue("Bundle-ActivationPolicy"))
                || "true".equals(getHeaderValue("Eclipse-LazyStart"))
                || "true".equals(getHeaderValue("Eclipse-AutoStart"))) {
            state = BundleState.LAZY_ACTIVATE;
            context = bundleContextFactory.createBundleContext(this);
        } else {
            state = BundleState.LOADED;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded bundle " + symbolicName + " with initial state " + state);
        }
    }

    @Override
    String getHeaderValue(String name) throws BundleException {
        String value = attrs.getValue(name);
        if (value == null) {
            return null;
        } else {
            Element[] elements;
            try {
                elements = Element.parseHeaderValue(value);
            } catch (ParseException ex) {
                throw new BundleException("Unable to parse " + name + " header", BundleException.MANIFEST_ERROR, ex);
            }
            if (elements.length != 1) {
                throw new BundleException("Expected only a single value for header " + name, BundleException.MANIFEST_ERROR);
            } else {
                return elements[0].getValue();
            }
        }
    }

    public long getBundleId() {
        return id;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public Version getVersion() {
        return version;
    }

    public int getState() {
        return state.getOsgiState();
    }

    public Dictionary<String,String> getHeaders() {
        Hashtable<String,String> headers = new Hashtable<String,String>();
        for (Map.Entry<Object,Object> entry : attrs.entrySet()) {
            headers.put(((Name)entry.getKey()).toString(), (String)entry.getValue());
        }
        return headers;
    }

    public Dictionary<String,String> getHeaders(String locale) {
        // TODO
        return getHeaders();
    }

    public void start(int options) throws BundleException {
        // TODO: check if this is correct
        start();
    }

    private void makeDependenciesReady() throws BundleException {
        String requireBundle = attrs.getValue("Require-Bundle");
        if (requireBundle != null) {
            Element[] elements;
            try {
                elements = Element.parseHeaderValue(requireBundle);
            } catch (ParseException ex) {
                throw new BundleException("Unable to parse Require-Bundle header", BundleException.MANIFEST_ERROR, ex);
            }
            for (Element element : elements) {
                BundleImpl bundle = (BundleImpl)bundleManager.getBundle(element.getValue());
                if (bundle != null) {
                    bundle.makeReady(USED_BY_BUNDLE, this);
                }
            }
        }
        String importPackage = attrs.getValue("Import-Package");
        if (importPackage != null) {
            Element[] elements;
            try {
                elements = Element.parseHeaderValue(importPackage);
            } catch (ParseException ex) {
                throw new BundleException("Unable to parse Import-Package header", BundleException.MANIFEST_ERROR, ex);
            }
            for (Element element : elements) {
                BundleImpl bundle = bundleManager.getBundleByPackage(element.getValue());
                // Note that a bundle can import a package from itself
                if (bundle != null && bundle != this) {
                    bundle.makeReady(USED_BY_BUNDLE, this);
                }
            }
        }
        state = BundleState.READY;
    }
    
    private <T> void makeReady(Reason<T> reason, T context) throws BundleException {
        switch (state) {
            case LOADED:
                if (logger.isDebugEnabled()) {
                    logger.debug("Need to make bundle " + symbolicName + " ready; reason: " + reason.format(context));
                }
                makeDependenciesReady();
                break;
            case LAZY_ACTIVATE:
                if (logger.isDebugEnabled()) {
                    logger.debug("Need to start bundle " + symbolicName + "; reason: " + reason.format(context));
                }
                start();
                break;
            default:
        }
    }
    
    public void start() throws BundleException {
        if (state == BundleState.ACTIVE) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Starting bundle " + symbolicName + " ...");
        }
        if (state == BundleState.LOADED || state == BundleState.LAZY_ACTIVATE) {
            makeDependenciesReady();
        }
        startOrder = nextStartOrder.getAndIncrement();
        state = BundleState.STARTING;
        bundleManager.fireBundleEvent(this, BundleEvent.STARTING);
        // For bundles with lazy activation, the BundleContext has already been created
        if (context == null) {
            context = bundleContextFactory.createBundleContext(this);
        }
        String activatorClassName = attrs.getValue("Bundle-Activator");
        if (activatorClassName != null) {
            try {
                activator = (BundleActivator)Class.forName(activatorClassName).newInstance();
            } catch (Exception ex) {
                throw new BundleException("Failed to instantiate bundle activator " + activatorClassName, ex);
            }
            try {
                activator.start(context);
            } catch (Exception ex) {
                throw new BundleException("Failed to start bundle " + symbolicName, ex);
            }
        }
        state = BundleState.ACTIVE;
        if (logger.isDebugEnabled()) {
            logger.debug("Bundle " + symbolicName + " started");
        }
        bundleManager.fireBundleEvent(this, BundleEvent.STARTED);
    }

    public void stop(int options) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void stop() throws BundleException {
        if (state == BundleState.LOADED || state == BundleState.READY) {
            return;
        }
        startOrder = -1;
        logger.debug("Stopping bundle {} ...", symbolicName);
        bundleManager.fireBundleEvent(this, BundleEvent.STOPPING);
        if (state == BundleState.ACTIVE && activator != null) {
            try {
                activator.stop(context);
            } catch (Exception ex) {
                throw new BundleException("Failed to stop bundle " + symbolicName, ex);
            }
        }
        context.destroy();
        // TODO: also unregister service and bundle listeners (or store them in the bundle context)
        context = null;
        state = BundleState.READY;
        bundleManager.fireBundleEvent(this, BundleEvent.STOPPED);
        logger.debug("Bundle {} stopped", symbolicName);
    }

    public URL getEntry(String path) {
        if (path.equals("/")) {
            return rootUrl;
        } else if (entries.get().contains(path)) {
            try {
                return new URL(rootUrl, path);
            } catch (MalformedURLException ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
        if (!recurse && filePattern.indexOf('*') == -1 && filePattern.indexOf('?') == -1) {
            Vector<URL> entries = new Vector<URL>();
            URL entry = getEntry(path + "/" + filePattern);
            if (entry != null) {
                entries.add(entry);
            }
            return entries.elements();
        } else {
            // TODO
            return new Vector<URL>().elements();
//            throw new UnsupportedOperationException();
        }
    }

    public URL getLocationUrl() {
        return locationUrl;
    }

    public String getLocation() {
        return locationUrl.toString();
    }

    @Override
    void beforeLoadClass(String name) throws BundleException {
        makeReady(CLASS_LOADING_REQUEST, name);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle.get();
    }

    @Override
    public <A> A adapt(Class<A> type) {
        if (type == BundleWiring.class) {
            return type.cast(wiring);
        } else {
            return super.adapt(type);
        }
    }

    int getStartOrder() {
        return startOrder;
    }

    @Override
    public List<BundleCapability> getDeclaredCapabilities(String namespace) {
        return bundleCapabilities.get(namespace);
    }

    @Override
    public List<BundleRequirement> getDeclaredRequirements(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTypes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BundleWiring getWiring() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Capability> getCapabilities(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStartLevel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStartLevel(int startlevel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPersistentlyStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isActivationPolicyUsed() {
        return true;
    }
}
