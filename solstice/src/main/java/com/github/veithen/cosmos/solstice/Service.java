package com.github.veithen.cosmos.solstice;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

final class Service implements CosmosServiceReference<Object>, ServiceRegistration<Object> {
    private final BundleImpl bundle;
    private final String[] classes;
    private final Object serviceObject;
    private final Dictionary<String,?> properties;
    private final Map<BundleImpl,ServiceContext> contexts = new HashMap<BundleImpl,ServiceContext>();
    
    Service(BundleImpl bundle, String[] classes, Object serviceObject, Dictionary<String,?> properties) {
        this.bundle = bundle;
        this.classes = classes;
        this.serviceObject = serviceObject;
        this.properties = properties;
    }
    
    boolean matches(String clazz, Filter filter) {
        if (clazz != null) {
            boolean classMatches = false;
            for (String c : classes) {
                if (c.equals(clazz)) {
                    classMatches = true;
                    break;
                }
            }
            if (!classMatches) {
                return false;
            }
        }
        return filter == null || filter.matchCase(properties);
    }

    Object getServiceObject() {
        return serviceObject;
    }

    public Object getService(BundleImpl bundle) {
        ServiceContext context = contexts.get(bundle);
        if (context == null) {
            context = new ServiceContext(this, bundle);
            contexts.put(bundle, context);
        }
        return context.getService();
    }

    public Object getProperty(String key) {
        return properties == null ? null : properties.get(key);
    }

    public String[] getPropertyKeys() {
        throw new UnsupportedOperationException();
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Bundle[] getUsingBundles() {
        throw new UnsupportedOperationException();
    }

    public boolean isAssignableTo(Bundle bundle, String className) {
        throw new UnsupportedOperationException();
    }

    public int compareTo(Object reference) {
        throw new UnsupportedOperationException();
    }

    public ServiceReference<Object> getReference() {
        return this;
    }

    public void setProperties(Dictionary<String,?> properties) {
        throw new UnsupportedOperationException();
    }

    public void unregister() {
        throw new UnsupportedOperationException();
    }
}
