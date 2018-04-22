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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    <S> Service<S> registerService(BundleImpl bundle, String[] classes, ServiceFactory<S> serviceFactory, Dictionary<String,?> properties) {
        long serviceId = nextServiceId++;
        if (logger.isDebugEnabled()) {
            Class<?> clazz = serviceFactory instanceof SingletonServiceFactory ? ((SingletonServiceFactory<S>)serviceFactory).getService().getClass() : serviceFactory.getClass();
            logger.debug("Registering service " + clazz.getName() + " with types " + Arrays.asList(classes) + " and properties " + properties + "; id is " + serviceId);
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
        Service<S> service = new Service<S>(this, bundle, classes, serviceFactory, actualProperties);
        synchronized (services) {
            services.add(service);
        }
        fireServiceChangedEvent(ServiceEvent.REGISTERED, service);
        return service;
    }

    void unregisterService(Service<?> service) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Unregistering service %s", service.getProperty(Constants.SERVICE_ID)));
        }
        fireServiceChangedEvent(ServiceEvent.UNREGISTERING, service);
        synchronized (services) {
            services.remove(service);
        }
    }

    void unregisterServices(BundleImpl bundle) {
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

    ServiceReference<?>[] getServiceReferences(String clazz, Filter filter) {
        List<ServiceReference<?>> references = new ArrayList<ServiceReference<?>>();
        for (Service<?> service : services) {
            if (service.matches(clazz, filter)) {
                references.add(service);
            }
        }
        return references.toArray(new ServiceReference<?>[references.size()]);
    }

    ServiceReference<?> getServiceReference(String clazz, Filter filter) {
        for (Service<?> service : services) {
            if (service.matches(clazz, filter)) {
                return service;
            }
        }
        return null;
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

    private void fireServiceChangedEvent(int type, Service<?> service) {
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
}
