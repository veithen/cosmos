/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2024 Andreas Veithen
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private final List<Service<?>> services = new LinkedList<Service<?>>();
    private final List<ServiceListenerSpec> serviceListeners = new ArrayList<>();
    private long nextServiceId = 1;

    <S> Service<S> registerService(
            AbstractBundle bundle,
            String[] classes,
            ServiceFactory<S> serviceFactory,
            Dictionary<String, ?> properties) {
        long serviceId = nextServiceId++;
        if (logger.isDebugEnabled()) {
            Class<?> clazz =
                    serviceFactory instanceof SingletonServiceFactory
                            ? ((SingletonServiceFactory<S>) serviceFactory).getService().getClass()
                            : serviceFactory.getClass();
            logger.debug(
                    "Bundle {} is registering service {} with types {} and properties {}; id is {}",
                    bundle.getSymbolicName(),
                    clazz.getName(),
                    Arrays.asList(classes),
                    properties,
                    serviceId);
        }
        Map<String, Object> actualProperties = new HashMap<>();
        if (properties != null) {
            for (Enumeration<String> keys = properties.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                actualProperties.put(key, properties.get(key));
            }
        }
        actualProperties.put(Constants.OBJECTCLASS, classes);
        actualProperties.put(Constants.SERVICE_ID, serviceId);
        actualProperties.put(Constants.SERVICE_BUNDLEID, bundle.getBundleId());
        Service<S> service =
                new Service<S>(this, bundle, classes, serviceFactory, actualProperties);
        synchronized (services) {
            services.add(service);
        }
        fireServiceChangedEvent(ServiceEvent.REGISTERED, service);
        return service;
    }

    void unregisterService(Service<?> service) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    String.format(
                            "Unregistering service %s", service.getProperty(Constants.SERVICE_ID)));
        }
        fireServiceChangedEvent(ServiceEvent.UNREGISTERING, service);
        synchronized (services) {
            services.remove(service);
        }
    }

    void unregisterServices(AbstractBundle bundle) {
        List<Service<?>> servicesToUnregister = new ArrayList<>();
        synchronized (services) {
            for (Service<?> service : services) {
                if (service.getBundle() == bundle) {
                    servicesToUnregister.add(service);
                }
            }
        }
        for (Service<?> service : servicesToUnregister) {
            unregisterService(service);
        }
    }

    <T> List<ServiceReference<T>> getServiceReferences(String clazz, Filter filter, Class<T> type) {
        List<ServiceReference<T>> references = new ArrayList<ServiceReference<T>>();
        for (Service<?> service : services) {
            if (service.matches(clazz, filter)) {
                references.add(service.getReference(type));
            }
        }
        return references;
    }

    <T> ServiceReference<T> getServiceReference(String clazz, Filter filter, Class<T> type) {
        int ranking = 0;
        Service<?> service = null;
        for (Service<?> candidate : services) {
            if (candidate.matches(clazz, filter)) {
                int candidateRanking = candidate.getRanking();
                if (service == null || candidateRanking > ranking) {
                    ranking = candidateRanking;
                    service = candidate;
                }
            }
        }
        return service == null ? null : service.getReference(type);
    }

    void addServiceListener(AbstractBundle bundle, ServiceListener listener, Filter filter) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "Bundle "
                            + bundle.getSymbolicName()
                            + " starts listening for services with filter "
                            + filter);
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

    void fireServiceChangedEvent(int type, Service<?> service) {
        ServiceListenerSpec[] serviceListeners;
        synchronized (this.serviceListeners) {
            serviceListeners =
                    this.serviceListeners.toArray(
                            new ServiceListenerSpec[this.serviceListeners.size()]);
        }
        for (ServiceListenerSpec listener : serviceListeners) {
            if (service.matches(null, listener.getFilter())) {
                listener.getListener()
                        .serviceChanged(new ServiceEvent(type, service.getReference()));
            }
        }
    }
}
