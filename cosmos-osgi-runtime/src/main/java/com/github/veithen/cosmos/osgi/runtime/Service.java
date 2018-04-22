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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

final class Service<S> implements CosmosServiceReference<S>, ServiceRegistration<S> {
    private final ServiceRegistry serviceRegistry;
    private final BundleImpl bundle;
    private final String[] classes;
    private final ServiceFactory<S> serviceFactory;
    private final Dictionary<String,?> properties;
    private final Map<BundleImpl,ServiceContext<S>> contexts = new HashMap<BundleImpl,ServiceContext<S>>();
    
    Service(ServiceRegistry serviceRegistry, BundleImpl bundle, String[] classes, ServiceFactory<S> serviceFactory, Dictionary<String,?> properties) {
        this.serviceRegistry = serviceRegistry;
        this.bundle = bundle;
        this.classes = classes;
        this.serviceFactory = serviceFactory;
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

    ServiceFactory<S> getServiceFactory() {
        return serviceFactory;
    }

    public S getService(BundleImpl bundle) {
        ServiceContext<S> context = contexts.get(bundle);
        if (context == null) {
            context = new ServiceContext<S>(this, bundle);
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

    public ServiceReference<S> getReference() {
        return this;
    }

    public void setProperties(Dictionary<String,?> properties) {
        throw new UnsupportedOperationException();
    }

    public void unregister() {
        serviceRegistry.unregisterService(this);
    }
}
