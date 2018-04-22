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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BundleImpl implements Bundle {
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
    
    private final CosmosRuntime runtime;
    private final long id;
    private final String symbolicName;
    private final Attributes attrs;
    private final URL rootUrl;
    private final URL locationUrl;
    private ServiceRegistry serviceRegistry;
    private BundleState state;
    private BundleContextImpl context;
    private BundleActivator activator;

    public BundleImpl(CosmosRuntime runtime, long id, String symbolicName, Attributes attrs, URL rootUrl) throws BundleException {
        this.runtime = runtime;
        this.id = id;
        this.symbolicName = symbolicName;
        this.attrs = attrs;
        this.rootUrl = rootUrl;
        if (rootUrl != null && rootUrl.getProtocol().equals("jar")) {
            String path = rootUrl.getPath();
            try {
                locationUrl = new URL(path.substring(0, path.lastIndexOf('!')));
            } catch (MalformedURLException ex) {
                throw new BundleException("Failed to extract bundle URL", ex);
            }
        } else {
            locationUrl = rootUrl;
        }
    }

    void initialize(ServiceRegistry serviceRegistry) throws BundleException {
        this.serviceRegistry = serviceRegistry;
        if (id == 0) {
            // The system bundle is always active.
            state = BundleState.ACTIVE;
        } else if ("lazy".equals(getHeaderValue("Bundle-ActivationPolicy"))
                || "true".equals(getHeaderValue("Eclipse-LazyStart"))
                || "true".equals(getHeaderValue("Eclipse-AutoStart"))) {
            state = BundleState.LAZY_ACTIVATE;
        } else {
            state = BundleState.LOADED;
        }
        if (state != BundleState.LOADED) {
            context = new BundleContextImpl(this, serviceRegistry);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded bundle " + symbolicName + " with initial state " + state);
        }
    }
    
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

    CosmosRuntime getRuntime() {
        return runtime;
    }

    public long getBundleId() {
        return id;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public Version getVersion() {
        return Version.parseVersion(attrs.getValue("Bundle-Version"));
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
        throw new UnsupportedOperationException();
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
                BundleImpl bundle = (BundleImpl)runtime.getBundle(element.getValue());
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
                BundleImpl bundle = runtime.getBundleByPackage(element.getValue());
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
        runtime.fireBundleEvent(this, BundleEvent.STARTING);
        // For bundles with lazy activation, the BundleContext has already been created
        if (context == null) {
            context = new BundleContextImpl(this, serviceRegistry);
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
        runtime.fireBundleEvent(this, BundleEvent.STARTED);
    }

    public void stop(int options) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void stop() throws BundleException {
        if (state == BundleState.LOADED || state == BundleState.READY) {
            return;
        }
        logger.debug("Stopping bundle {} ...", symbolicName);
        runtime.fireBundleEvent(this, BundleEvent.STOPPING);
        if (state == BundleState.ACTIVE && activator != null) {
            try {
                activator.stop(context);
            } catch (Exception ex) {
                throw new BundleException("Failed to stop bundle " + symbolicName, ex);
            }
        }
        serviceRegistry.unregisterServices(this);
        // TODO: also unregister service and bundle listeners (or store them in the bundle context)
        context = null;
        state = BundleState.READY;
        runtime.fireBundleEvent(this, BundleEvent.STOPPED);
        logger.debug("Bundle {} stopped", symbolicName);
    }

    public int compareTo(Bundle o) {
        throw new UnsupportedOperationException();
    }

    public void update(InputStream input) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void update() throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void uninstall() throws BundleException {
        throw new UnsupportedOperationException();
    }

    public URL getEntry(String path) {
        // TODO: not correct; need to return null if entry doesn't exist
        try {
            return new URL(rootUrl, path);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    public Enumeration<String> getEntryPaths(String path) {
        throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }
    }

    public URL getLocationUrl() {
        return locationUrl;
    }

    public String getLocation() {
        return locationUrl.toString();
    }

    public ServiceReference<?>[] getRegisteredServices() {
        throw new UnsupportedOperationException();
    }

    public ServiceReference<?>[] getServicesInUse() {
        throw new UnsupportedOperationException();
    }

    public boolean hasPermission(Object permission) {
        throw new UnsupportedOperationException();
    }

    public URL getResource(String name) {
        throw new UnsupportedOperationException();
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            makeReady(CLASS_LOADING_REQUEST, name);
        } catch (BundleException ex) {
            throw new ClassNotFoundException(name, ex);
        }
        return Class.forName(name);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    public long getLastModified() {
        // We never modify bundles, so we may as well return 0 here
        return 0;
    }

    public BundleContext getBundleContext() {
        return context;
    }

    public Map<X509Certificate,List<X509Certificate>> getSignerCertificates(int signersType) {
        throw new UnsupportedOperationException();
    }

    public <A> A adapt(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    public File getDataFile(String filename) {
        // We don't have filesystem support.
        return null;
    }

    void distributeBundleEvent(BundleEvent event) {
        if (context != null) {
            context.distributeBundleEvent(event);
        }
    }
}
