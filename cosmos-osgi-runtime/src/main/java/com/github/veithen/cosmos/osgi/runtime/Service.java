/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2022 Andreas Veithen
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

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Service<S> implements ServiceRegistration<S> {
    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    private static final Set<String> nonUpdateableProperties =
            new HashSet<>(
                    Arrays.asList(
                            Constants.OBJECTCLASS,
                            Constants.SERVICE_BUNDLEID,
                            Constants.SERVICE_ID,
                            Constants.SERVICE_SCOPE));

    private final ServiceRegistry serviceRegistry;
    private final AbstractBundle bundle;
    private final String[] classes;
    private final ServiceFactory<S> serviceFactory;
    private final Dictionary<String, Object> properties;
    private final Map<AbstractBundle, ServiceContext<S>> contexts = new HashMap<>();
    private final ServiceReference<S> reference;

    Service(
            ServiceRegistry serviceRegistry,
            AbstractBundle bundle,
            String[] classes,
            ServiceFactory<S> serviceFactory,
            Dictionary<String, Object> properties) {
        this.serviceRegistry = serviceRegistry;
        this.bundle = bundle;
        this.classes = classes;
        this.serviceFactory = serviceFactory;
        this.properties = properties;
        reference =
                new ServiceReferenceImpl<S>(this) {
                    @Override
                    S getService(AbstractBundle bundle) {
                        return Service.this.getService(bundle);
                    }
                };
    }

    boolean matches(String clazz, Filter filter) {
        synchronized (properties) {
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
    }

    ServiceFactory<S> getServiceFactory() {
        return serviceFactory;
    }

    S getService(AbstractBundle bundle) {
        ServiceContext<S> context = contexts.get(bundle);
        if (context == null) {
            context = new ServiceContext<S>(this, bundle);
            contexts.put(bundle, context);
        }
        return context.getService();
    }

    Object getProperty(String key) {
        synchronized (properties) {
            return properties == null ? null : properties.get(key);
        }
    }

    Bundle getBundle() {
        return bundle;
    }

    @Override
    public ServiceReference<S> getReference() {
        return reference;
    }

    <T> ServiceReference<T> getReference(final Class<T> type) {
        return new ServiceReferenceImpl<T>(this) {
            @Override
            T getService(AbstractBundle bundle) {
                return type.cast(Service.this.getService(bundle));
            }
        };
    }

    @Override
    public void setProperties(Dictionary<String, ?> newProperties) {
        synchronized (properties) {
            for (Enumeration<String> keys = newProperties.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                if (!nonUpdateableProperties.contains(key)) {
                    properties.put(key, newProperties.get(key));
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Updated properties of service {}; new properties: {}",
                        getId(),
                        properties);
            }
        }
        serviceRegistry.fireServiceChangedEvent(ServiceEvent.MODIFIED, this);
    }

    @Override
    public void unregister() {
        serviceRegistry.unregisterService(this);
    }

    long getId() {
        synchronized (properties) {
            return (Long) properties.get(Constants.SERVICE_ID);
        }
    }

    int getRanking() {
        synchronized (properties) {
            Integer ranking = (Integer) properties.get(Constants.SERVICE_RANKING);
            return ranking == null ? 0 : ranking;
        }
    }
}
