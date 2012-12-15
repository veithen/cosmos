package cosmos;

import java.io.File;
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class Runtime {
    private static Runtime instance;

    private final Properties properties = new Properties();
    private final Map<String,BundleImpl> bundlesBySymbolicName = new HashMap<String,BundleImpl>();
    private final Map<Long,BundleImpl> bundlesById = new HashMap<Long,BundleImpl>();
    private final List<BundleListener> bundleListeners = new LinkedList<BundleListener>();
    private final List<ServiceListenerSpec> serviceListeners = new LinkedList<ServiceListenerSpec>();
    private final List<Service> services = new LinkedList<Service>();
    private long serviceId = 1;
    private final File dataRoot;

    private Runtime() throws CosmosException {
        // TODO: make this configurable
        dataRoot = new File("target/osgi");
        Enumeration<URL> e;
        try {
            e = Runtime.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        } catch (IOException ex) {
            throw new CosmosException("Failed to load manifests", ex);
        }
        long bundleId = 1;
        while (e.hasMoreElements()) {
            URL url = e.nextElement();
            Manifest manifest;
            try {
                InputStream in = url.openStream();
                try {
                    manifest = new Manifest(in);
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new CosmosException("Failed to read " + url, ex);
            }
            Attributes attrs = manifest.getMainAttributes();
            String symbolicName = attrs.getValue("Bundle-SymbolicName");
            if (symbolicName == null) {
                continue;
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
                throw new CosmosException("Unexpected exception", ex);
            }
            // There cannot be any bundle listeners yet, so no need to call BundleListeners
            long id = bundleId++;
            BundleImpl bundle = new BundleImpl(this, id, symbolicName, attrs, rootUrl, new File(dataRoot, symbolicName));
            bundlesBySymbolicName.put(symbolicName, bundle);
            bundlesById.put(id, bundle);
        }
    }
    
    public static synchronized Runtime getInstance() throws CosmosException {
        if (instance == null) {
            instance = new Runtime();
        }
        return instance;
    }
    
    public Bundle[] getBundles() {
        Collection<BundleImpl> c = bundlesBySymbolicName.values();
        return c.toArray(new Bundle[c.size()]);
    }
    
    public Bundle getBundle(String symbolicName) {
        return bundlesBySymbolicName.get(symbolicName);
    }
    
    Bundle getBundle(long id) {
        return bundlesById.get(id);
    }
    
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.getProperty(key);
        }
        if (value == null) {
            System.out.println("No value for property " + key);
        }
        return value;
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public void addBundleListener(BundleListener listener) {
        bundleListeners.add(listener);
    }

    public void addServiceListener(ServiceListener listener, Filter filter) {
        serviceListeners.add(new ServiceListenerSpec(listener, filter));
    }

    public void removeServiceListener(ServiceListener listener) {
        for (Iterator<ServiceListenerSpec> it = serviceListeners.iterator(); it.hasNext(); ) {
            if (it.next().getListener() == listener) {
                it.remove();
            }
        }
    }

    public <T> void registerService(BundleImpl bundle, String[] classes, Object serviceObject, Dictionary<String,?> properties) {
        System.out.println("registerService: " + Arrays.asList(classes));
        Hashtable<String,Object> actualProperties = new Hashtable<String,Object>();
        if (properties != null) {
            for (Enumeration<String> keys = properties.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                actualProperties.put(key, properties.get(key));
            }
        }
        actualProperties.put(Constants.SERVICE_ID, serviceId++);
        Service service = new Service(bundle, classes, serviceObject, actualProperties);
        services.add(service);
        for (ServiceListenerSpec listener : serviceListeners) {
            if (service.matches(null, listener.getFilter())) {
                listener.getListener().serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, service));
            }
        }
    }

    public ServiceReference<?>[] getServiceReferences(String clazz, Filter filter) {
        List<ServiceReference<?>> references = new ArrayList<ServiceReference<?>>();
        for (Service service : services) {
            if (service.matches(clazz, filter)) {
                references.add(service);
            }
        }
        return references.toArray(new ServiceReference<?>[references.size()]);
    }

    public ServiceReference<?> getServiceReference(String clazz, Filter filter) {
        List<ServiceReference<?>> references = new ArrayList<ServiceReference<?>>();
        for (Service service : services) {
            if (service.matches(clazz, filter)) {
                return service;
            }
        }
        return null;
    }

    void fireBundleEvent(BundleImpl bundleImpl, int type) {
        BundleEvent event = new BundleEvent(type, bundleImpl);
        for (BundleListener listener : bundleListeners) {
            listener.bundleChanged(event);
        }
    }
}
