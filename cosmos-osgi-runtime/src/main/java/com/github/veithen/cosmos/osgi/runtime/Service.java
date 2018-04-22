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

final class Service<S> implements ServiceRegistration<S> {
    private final ServiceRegistry serviceRegistry;
    private final BundleImpl bundle;
    private final String[] classes;
    private final ServiceFactory<S> serviceFactory;
    private final Dictionary<String,?> properties;
    private final Map<BundleImpl,ServiceContext<S>> contexts = new HashMap<BundleImpl,ServiceContext<S>>();
    private final ServiceReference<S> reference;
    
    Service(ServiceRegistry serviceRegistry, BundleImpl bundle, String[] classes, ServiceFactory<S> serviceFactory, Dictionary<String,?> properties) {
        this.serviceRegistry = serviceRegistry;
        this.bundle = bundle;
        this.classes = classes;
        this.serviceFactory = serviceFactory;
        this.properties = properties;
        reference = new ServiceReferenceImpl<S>(this) {
            @Override
            S getService(BundleImpl bundle) {
                return Service.this.getService(bundle);
            }
        };
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

    S getService(BundleImpl bundle) {
        ServiceContext<S> context = contexts.get(bundle);
        if (context == null) {
            context = new ServiceContext<S>(this, bundle);
            contexts.put(bundle, context);
        }
        return context.getService();
    }

    Object getProperty(String key) {
        return properties == null ? null : properties.get(key);
    }

    Bundle getBundle() {
        return bundle;
    }

    public ServiceReference<S> getReference() {
        return reference;
    }

    <T> ServiceReference<T> getReference(final Class<T> type) {
        return new ServiceReferenceImpl<T>(this) {
            @Override
            T getService(BundleImpl bundle) {
                return type.cast(Service.this.getService(bundle));
            }
        };
    }

    public void setProperties(Dictionary<String,?> properties) {
        throw new UnsupportedOperationException();
    }

    public void unregister() {
        serviceRegistry.unregisterService(this);
    }
}
