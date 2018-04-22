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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.xml.XMLParserActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.veithen.cosmos.osgi.runtime.internal.InternalLoggerFactory;

public final class CosmosRuntime {
    private static final Logger logger = LoggerFactory.getLogger(CosmosRuntime.class);

    private static CosmosRuntime instance;

    private final Properties properties = new Properties();
    private final BundleImpl[] bundles;
    private final Map<String,BundleImpl> bundlesBySymbolicName = new HashMap<String,BundleImpl>();
    private final ServiceRegistry serviceRegistry;
    
    /**
     * Maps exported packages to their corresponding bundles.
     */
    private final Map<String,BundleImpl> packageMap = new HashMap<String,BundleImpl>();

    private CosmosRuntime() throws BundleException {
        final List<BundleImpl> bundles = new ArrayList<>();
        final Map<URL,Bundle> bundlesByUrl = new HashMap<URL,Bundle>();
        // Add a system bundle
        // TODO: this should implement org.osgi.framework.launch.Framework
        BundleImpl systemBundle = new BundleImpl(this, 0, Constants.SYSTEM_BUNDLE_SYMBOLICNAME, new Attributes(), null);
        bundles.add(systemBundle);
        ResourceUtil.processResources("META-INF/MANIFEST.MF", new ResourceProcessor() {
            @Override
            public void process(URL url, InputStream in) throws IOException, BundleException {
                Manifest manifest = new Manifest(in);
                Attributes attrs = manifest.getMainAttributes();
                String symbolicName = attrs.getValue("Bundle-SymbolicName");
                if (symbolicName == null) {
                    return;
                }
                // Remove the "singleton" attribute
                int idx = symbolicName.indexOf(';');
                if (idx != -1) {
                    symbolicName = symbolicName.substring(0, idx);
                }
                URL rootUrl;
                try {
                    rootUrl = new URL(url, "..");
                } catch (MalformedURLException ex) {
                    throw new BundleException("Unexpected exception", ex);
                }
                // There cannot be any bundle listeners yet, so no need to call BundleListeners
                BundleImpl bundle = new BundleImpl(CosmosRuntime.this, bundles.size(), symbolicName, attrs, rootUrl);
                bundles.add(bundle);
                bundlesBySymbolicName.put(symbolicName, bundle);
                bundlesByUrl.put(bundle.getLocationUrl(), bundle);
                String exportPackage = attrs.getValue("Export-Package");
                if (exportPackage != null) {
                    Element[] elements;
                    try {
                        elements = Element.parseHeaderValue(exportPackage);
                    } catch (ParseException ex) {
                        throw new BundleException("Unable to parse Export-Package header", BundleException.MANIFEST_ERROR, ex);
                    }
                    for (Element element : elements) {
                        // TODO: what if the same package is exported by multiple bundles??
                        packageMap.put(element.getValue(), bundle);
                    }
                }
            }
        });
        this.bundles = bundles.toArray(new BundleImpl[bundles.size()]);
        serviceRegistry = new ServiceRegistry();
        for (BundleImpl bundle : bundles) {
            bundle.initialize(serviceRegistry);
        }
        Patcher.injectBundles(bundlesByUrl);
        loadProperties("META-INF/cosmos.properties");
        if (logger.isDebugEnabled()) {
            loadProperties("META-INF/cosmos-debug.properties");
            logger.debug(String.format("Properties: %s", properties));
        }
        registerSAXParserFactory(systemBundle);
        registerDocumentBuilderFactory(systemBundle);
        systemBundle.getBundleContext().registerService(Logger.class, logger, null);
        InternalLoggerFactory internalLoggerFactory = new InternalLoggerFactoryImpl();
        systemBundle.getBundleContext().registerService(InternalLoggerFactory.class, internalLoggerFactory, null);
        systemBundle.getBundleContext().registerService(LogService.class, new LogServiceFactory(internalLoggerFactory), null);
        final Set<Bundle> autostartBundles = new HashSet<>();
        for (BundleImpl bundle : bundles) {
            if ("true".equals(bundle.getHeaderValue("Cosmos-AutoStart"))) {
                autostartBundles.add(bundle);
            }
        }
        ResourceUtil.processResources("META-INF/cosmos-autostart-bundles.list", new ResourceProcessor() {
            @Override
            public void process(URL url, InputStream in) throws IOException, BundleException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        Bundle bundle = getBundle(line);
                        if (bundle == null) {
                            throw new BundleException(String.format("Bundle %s listed in %s not found", line, url));
                        }
                        autostartBundles.add(bundle);
                    }
                }
            }
        });
        Bundle scrBundle = getBundle("org.apache.felix.scr");
        if (scrBundle != null) {
            // Always auto-start the Declarative Services implementation if it's available. Do this
            // immediately so that declarative services are registered before other bundles start
            // (Not all bundles are designed to handle services becoming available after they
            // start).
            scrBundle.start();
        }
        for (Bundle bundle : autostartBundles) {
            bundle.start();
        }
    }

    private void registerSAXParserFactory(Bundle bundle) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        Hashtable<String,Object> props = new Hashtable<String,Object>();
        new XMLParserActivator().setSAXProperties(factory, props);
        bundle.getBundleContext().registerService(SAXParserFactory.class, factory, props);
    }
    
    private void registerDocumentBuilderFactory(Bundle bundle) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Hashtable<String,Object> props = new Hashtable<String,Object>();
        new XMLParserActivator().setDOMProperties(factory, props);
        bundle.getBundleContext().registerService(DocumentBuilderFactory.class, factory, props);
    }
    
    private void loadProperties(String resourceName) throws BundleException {
        ResourceUtil.processResources(resourceName, new ResourceProcessor() {
            @Override
            public void process(URL url, InputStream in) throws IOException {
                properties.load(in);
            }
        });
    }
    
    public static synchronized CosmosRuntime getInstance() throws BundleException {
        if (instance == null) {
            Patcher.patch();
            instance = new CosmosRuntime();
        }
        return instance;
    }
    
    Bundle[] getBundles() {
        return bundles.clone();
    }
    
    Bundle getBundle(String symbolicName) {
        return bundlesBySymbolicName.get(symbolicName);
    }
    
    BundleImpl getBundleByPackage(String pkg) {
        return packageMap.get(pkg);
    }
    
    Bundle getBundle(long id) {
        return id < bundles.length ? bundles[(int)id] : null;
    }
    
    String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.getProperty(key);
        }
        if (value == null && logger.isDebugEnabled()) {
            logger.debug("No value for property " + key);
        }
        return value;
    }
    
    public <T> T getService(Class<T> clazz) {
        ServiceReference<T> ref = serviceRegistry.getServiceReference(clazz.getName(), null, clazz);
        // TODO: need a system/framework bundle here
        return ref == null ? null : ((ServiceReferenceImpl<T>)ref).getService(null);
    }
    
    void fireBundleEvent(BundleImpl bundleImpl, int type) {
        BundleEvent event = new BundleEvent(type, bundleImpl);
        for (BundleImpl bundle : bundles) {
            bundle.distributeBundleEvent(event);
        }
    }

    public void dispose() {
        for (BundleImpl bundle : bundles) {
            try {
                bundle.stop();
            } catch (BundleException ex) {
                logger.warn(String.format("Failed to stop bundle %s", bundle.getSymbolicName()), ex);
            }
        }
        synchronized (CosmosRuntime.class) {
            instance = null;
        }
    }
}
