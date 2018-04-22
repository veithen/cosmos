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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class BundleContextImpl implements BundleContext {
    private final BundleImpl bundle;
    private final ServiceRegistry serviceRegistry;
    private final List<BundleListener> bundleListeners = new LinkedList<BundleListener>();
    
    public BundleContextImpl(BundleImpl bundle, ServiceRegistry serviceRegistry) {
        this.bundle = bundle;
        this.serviceRegistry = serviceRegistry;
    }

    public String getProperty(String key) {
        return bundle.getRuntime().getProperty(key);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Bundle installBundle(String location, InputStream input) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public Bundle installBundle(String location) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public Bundle getBundle(long id) {
        return bundle.getRuntime().getBundle(id);
    }

    public Bundle[] getBundles() {
        return bundle.getRuntime().getBundles();
    }

    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        serviceRegistry.addServiceListener(bundle, listener, FrameworkUtil.createFilter(filter));
    }

    public void addServiceListener(ServiceListener listener) {
        serviceRegistry.addServiceListener(bundle, listener, null);
    }

    public void removeServiceListener(ServiceListener listener) {
        serviceRegistry.removeServiceListener(listener);
    }

    public void addBundleListener(BundleListener listener) {
        synchronized (bundleListeners) {
            bundleListeners.add(listener);
        }
    }

    public void removeBundleListener(BundleListener listener) {
        synchronized (bundleListeners) {
            bundleListeners.remove(listener);
        }
    }

    void distributeBundleEvent(BundleEvent event) {
        BundleListener[] bundleListeners;
        synchronized (this.bundleListeners) {
            bundleListeners = this.bundleListeners.toArray(new BundleListener[this.bundleListeners.size()]);
        }
        for (BundleListener listener : bundleListeners) {
            listener.bundleChanged(event);
        }
    }

    public void addFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException();
    }

    public void removeFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException();
    }

    public ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String,?> properties) {
        return serviceRegistry.registerService(bundle, clazzes, service, properties);
    }

    public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String,?> properties) {
        return serviceRegistry.registerService(bundle, new String[] { clazz }, service, properties);
    }

    public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String,?> properties) {
        return (ServiceRegistration<S>)serviceRegistry.registerService(bundle, new String[] { clazz.getName() }, service, properties);
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory, Dictionary<String,?> properties) {
        return (ServiceRegistration<S>)serviceRegistry.registerService(bundle, new String[] { clazz.getName() }, factory, properties);
    }

    public ServiceReference<?> getServiceReference(String clazz) {
        return serviceRegistry.getServiceReference(clazz, null);
    }

    public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
        return (ServiceReference<S>)getServiceReference(clazz.getName());
    }

    public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return serviceRegistry.getServiceReferences(clazz, filter == null ? null : FrameworkUtil.createFilter(filter));
    }

    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter) throws InvalidSyntaxException {
        return (Collection<ServiceReference<S>>)(Collection<?>)Arrays.asList(serviceRegistry.getServiceReferences(clazz.getName(), filter == null ? null : FrameworkUtil.createFilter(filter)));
    }

    public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // Since we don't have per bundle class loaders, getAllServiceReferences is the same as getServiceReferences.
        return getServiceReferences(clazz, filter);
    }

    public <S> S getService(ServiceReference<S> reference) {
        return ((CosmosServiceReference<S>)reference).getService(bundle);
    }

    @Override
    public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
        throw new UnsupportedOperationException();
    }

    public boolean ungetService(ServiceReference<?> reference) {
//        throw new UnsupportedOperationException();
        return true;
    }

    public File getDataFile(String filename) {
        return bundle.getDataFile(filename);
    }

    public Filter createFilter(String filter) throws InvalidSyntaxException {
        return FrameworkUtil.createFilter(filter);
    }

    public Bundle getBundle(String location) {
        throw new UnsupportedOperationException();
    }
}
