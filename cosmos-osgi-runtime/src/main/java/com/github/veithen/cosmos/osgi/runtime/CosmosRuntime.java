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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.xml.XMLParserActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CosmosRuntime {
    private static final Logger logger = LoggerFactory.getLogger(CosmosRuntime.class);

    private static CosmosRuntime instance;

    private final Properties properties = new Properties();
    private final Map<String,BundleImpl> bundlesBySymbolicName = new HashMap<String,BundleImpl>();
    private final Map<Long,BundleImpl> bundlesById = new HashMap<Long,BundleImpl>();
    private final List<BundleListener> bundleListeners = new LinkedList<BundleListener>();
    private final List<ServiceListenerSpec> serviceListeners = new ArrayList<>();
    private final List<Service> services = new LinkedList<Service>();
    private long nextServiceId = 1;
    
    /**
     * Maps exported packages to their corresponding bundles.
     */
    private final Map<String,BundleImpl> packageMap = new HashMap<String,BundleImpl>();

    private CosmosRuntime() throws BundleException {
        final Set<Bundle> autostartBundles = new HashSet<>();
        final Map<URL,Bundle> bundlesByUrl = new HashMap<URL,Bundle>();
        ResourceUtil.processResources("META-INF/MANIFEST.MF", new ResourceProcessor() {
            private long bundleId = 1;

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
                long id = bundleId++;
                BundleImpl bundle = new BundleImpl(CosmosRuntime.this, id, symbolicName, attrs, rootUrl);
                bundlesBySymbolicName.put(symbolicName, bundle);
                bundlesById.put(id, bundle);
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
                if ("true".equals(attrs.getValue("Cosmos-AutoStart"))) {
                    autostartBundles.add(bundle);
                }
            }
        });
        Patcher.injectBundles(bundlesByUrl);
        loadProperties("META-INF/cosmos.properties");
        if (logger.isDebugEnabled()) {
            loadProperties("META-INF/cosmos-debug.properties");
            logger.debug(String.format("Properties: %s", properties));
        }
        registerSAXParserFactory();
        registerDocumentBuilderFactory();
        registerService(null, new String[] { Logger.class.getName() }, logger, null);
        // TODO: make this a ServiceFactory and use a per bundle SLF4J logger
        registerService(null, new String[] { LogService.class.getName() }, new LogServiceAdapter(LoggerFactory.getLogger("osgi")), null);
        // Always auto-start the Declarative Services implementation if it's available
        Bundle scrBundle = getBundle("org.apache.felix.scr");
        if (scrBundle != null) {
            autostartBundles.add(scrBundle);
        }
        for (Bundle bundle : autostartBundles) {
            bundle.start();
        }
    }

    private void registerSAXParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        Hashtable<String,Object> props = new Hashtable<String,Object>();
        new XMLParserActivator().setSAXProperties(factory, props);
        registerService(null, new String[] { SAXParserFactory.class.getName() }, factory, props);
    }
    
    private void registerDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Hashtable<String,Object> props = new Hashtable<String,Object>();
        new XMLParserActivator().setDOMProperties(factory, props);
        registerService(null, new String[] { DocumentBuilderFactory.class.getName() }, factory, props);
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
        Collection<BundleImpl> c = bundlesBySymbolicName.values();
        return c.toArray(new Bundle[c.size()]);
    }
    
    Bundle getBundle(String symbolicName) {
        return bundlesBySymbolicName.get(symbolicName);
    }
    
    BundleImpl getBundleByPackage(String pkg) {
        return packageMap.get(pkg);
    }
    
    Bundle getBundle(long id) {
        return bundlesById.get(id);
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
    
    void addBundleListener(BundleListener listener) {
        bundleListeners.add(listener);
    }

    void addServiceListener(BundleImpl bundle, ServiceListener listener, Filter filter) {
        if (logger.isDebugEnabled()) {
            logger.debug("Bundle " + bundle.getSymbolicName() + " starts listening for services with filter " + filter);
        }
        synchronized (serviceListeners) {
            serviceListeners.add(new ServiceListenerSpec(listener, filter));
        }
    }

    void removeServiceListener(ServiceListener listener) {
        synchronized (serviceListeners) {
            for (Iterator<ServiceListenerSpec> it = serviceListeners.iterator(); it.hasNext(); ) {
                if (it.next().getListener() == listener) {
                    it.remove();
                }
            }
        }
    }

    public <T> ServiceRegistration<T> registerService(String[] classes, T serviceObject, Dictionary<String,?> properties) {
        return registerService((Bundle)null, classes, serviceObject, properties);
    }
    
    public <T> ServiceRegistration<T> registerService(Bundle bundle, String[] classes, T serviceObject, Dictionary<String,?> properties) {
        return (ServiceRegistration<T>)registerService((BundleImpl)bundle, classes, serviceObject, properties);
    }
    
    private void fireServiceChangedEvent(int type, Service service) {
        ServiceListenerSpec[] serviceListeners;
        synchronized (this.serviceListeners) {
            serviceListeners = this.serviceListeners.toArray(new ServiceListenerSpec[this.serviceListeners.size()]);
        }
        for (ServiceListenerSpec listener : serviceListeners) {
            if (service.matches(null, listener.getFilter())) {
                listener.getListener().serviceChanged(new ServiceEvent(type, service));
            }
        }
    }
    
    Service registerService(BundleImpl bundle, String[] classes, Object serviceObject, Dictionary<String,?> properties) {
        long serviceId = nextServiceId++;
        if (logger.isDebugEnabled()) {
            logger.debug("Registering service " + serviceObject.getClass().getName() + " with types " + Arrays.asList(classes) + " and properties " + properties + "; id is " + serviceId);
        }
        Hashtable<String,Object> actualProperties = new Hashtable<String,Object>();
        if (properties != null) {
            for (Enumeration<String> keys = properties.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                actualProperties.put(key, properties.get(key));
            }
        }
        actualProperties.put(Constants.OBJECTCLASS, classes);
        actualProperties.put(Constants.SERVICE_ID, serviceId);
        Service service = new Service(this, bundle, classes, serviceObject, actualProperties);
        synchronized (services) {
            services.add(service);
        }
        fireServiceChangedEvent(ServiceEvent.REGISTERED, service);
        return service;
    }

    void unregisterService(Service service) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Unregistering service %s", service.getProperty(Constants.SERVICE_ID)));
        }
        fireServiceChangedEvent(ServiceEvent.UNREGISTERING, service);
        synchronized (services) {
            services.remove(service);
        }
    }

    void unregisterServices(BundleImpl bundle) {
        List<Service> servicesToUnregister = new ArrayList<>();
        synchronized (services) {
            for (Service service : services) {
                if (service.getBundle() == bundle) {
                    servicesToUnregister.add(service);
                }
            }
        }
        for (Service service : servicesToUnregister) {
            unregisterService(service);
        }
    }

    ServiceReference<?>[] getServiceReferences(String clazz, Filter filter) {
        List<ServiceReference<?>> references = new ArrayList<ServiceReference<?>>();
        for (Service service : services) {
            if (service.matches(clazz, filter)) {
                references.add(service);
            }
        }
        return references.toArray(new ServiceReference<?>[references.size()]);
    }

    ServiceReference<?> getServiceReference(String clazz, Filter filter) {
        List<ServiceReference<?>> references = new ArrayList<ServiceReference<?>>();
        for (Service service : services) {
            if (service.matches(clazz, filter)) {
                return service;
            }
        }
        return null;
    }

    public <T> T getService(Class<T> clazz) {
        ServiceReference<?> ref = getServiceReference(clazz.getName(), null);
        // TODO: need a system/framework bundle here
        return ref == null ? null : clazz.cast(((Service)ref).getService(null));
    }
    
    void fireBundleEvent(BundleImpl bundleImpl, int type) {
        BundleEvent event = new BundleEvent(type, bundleImpl);
        for (BundleListener listener : bundleListeners) {
            listener.bundleChanged(event);
        }
    }

    public void dispose() {
        for (BundleImpl bundle : bundlesBySymbolicName.values()) {
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
