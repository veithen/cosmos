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

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.github.veithen.cosmos.osgi.runtime.internal.InternalBundleContext;

final class BundleContextImpl implements InternalBundleContext {
    private final AbstractBundle bundle;
    private final CosmosRuntime runtime;
    private final BundleManager bundleManager;
    private final ServiceRegistry serviceRegistry;
    private final List<BundleListener> bundleListeners = new LinkedList<BundleListener>();
    private BundleActivator activator;

    BundleContextImpl(
            AbstractBundle bundle,
            CosmosRuntime runtime,
            BundleManager bundleManager,
            ServiceRegistry serviceRegistry) {
        this.bundle = bundle;
        this.runtime = runtime;
        this.bundleManager = bundleManager;
        this.serviceRegistry = serviceRegistry;
    }

    void setActivator(BundleActivator activator) {
        this.activator = activator;
    }

    @Override
    public String getProperty(String key) {
        return runtime.getProperty(key);
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public Bundle installBundle(String location, InputStream input) throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle installBundle(String location) throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getBundle(long id) {
        return bundleManager.getBundle(id);
    }

    @Override
    public Bundle[] getBundles() {
        return bundleManager.getBundles();
    }

    @Override
    public void addServiceListener(ServiceListener listener, String filter)
            throws InvalidSyntaxException {
        serviceRegistry.addServiceListener(bundle, listener, FrameworkUtil.createFilter(filter));
    }

    @Override
    public void addServiceListener(ServiceListener listener) {
        serviceRegistry.addServiceListener(bundle, listener, null);
    }

    @Override
    public void removeServiceListener(ServiceListener listener) {
        serviceRegistry.removeServiceListener(listener);
    }

    @Override
    public void addBundleListener(BundleListener listener) {
        synchronized (bundleListeners) {
            bundleListeners.add(listener);
        }
    }

    @Override
    public void removeBundleListener(BundleListener listener) {
        synchronized (bundleListeners) {
            bundleListeners.remove(listener);
        }
    }

    void distributeBundleEvent(BundleEvent event) {
        BundleListener[] bundleListeners;
        synchronized (this.bundleListeners) {
            bundleListeners =
                    this.bundleListeners.toArray(new BundleListener[this.bundleListeners.size()]);
        }
        for (BundleListener listener : bundleListeners) {
            listener.bundleChanged(event);
        }
    }

    @Override
    public void addFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServiceRegistration<?> registerService(
            String[] clazzes, Object service, Dictionary<String, ?> properties) {
        if (service instanceof ServiceFactory<?>) {
            return serviceRegistry.registerService(
                    bundle, clazzes, (ServiceFactory<?>) service, properties);
        } else {
            return serviceRegistry.registerService(
                    bundle, clazzes, new SingletonServiceFactory<Object>(service), properties);
        }
    }

    @Override
    public ServiceRegistration<?> registerService(
            String clazz, Object service, Dictionary<String, ?> properties) {
        return registerService(new String[] {clazz}, service, properties);
    }

    @Override
    public <S> ServiceRegistration<S> registerService(
            Class<S> clazz, S service, Dictionary<String, ?> properties) {
        return serviceRegistry.registerService(
                bundle,
                new String[] {clazz.getName()},
                new SingletonServiceFactory<S>(service),
                properties);
    }

    @Override
    public <S> ServiceRegistration<S> registerService(
            Class<S> clazz, ServiceFactory<S> factory, Dictionary<String, ?> properties) {
        return serviceRegistry.registerService(
                bundle, new String[] {clazz.getName()}, factory, properties);
    }

    @Override
    public ServiceReference<?> getServiceReference(String clazz) {
        return serviceRegistry.getServiceReference(clazz, null, Object.class);
    }

    @Override
    public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
        return serviceRegistry.getServiceReference(clazz.getName(), null, clazz);
    }

    @Override
    public ServiceReference<?>[] getServiceReferences(String clazz, String filter)
            throws InvalidSyntaxException {
        List<ServiceReference<Object>> references =
                serviceRegistry.getServiceReferences(
                        clazz,
                        filter == null ? null : FrameworkUtil.createFilter(filter),
                        Object.class);
        return references.isEmpty()
                ? null
                : references.toArray(new ServiceReference<?>[references.size()]);
    }

    @Override
    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter)
            throws InvalidSyntaxException {
        return serviceRegistry.getServiceReferences(
                clazz.getName(), filter == null ? null : FrameworkUtil.createFilter(filter), clazz);
    }

    @Override
    public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter)
            throws InvalidSyntaxException {
        // Since we don't have per bundle class loaders, getAllServiceReferences is the same as
        // getServiceReferences.
        return getServiceReferences(clazz, filter);
    }

    @Override
    public <S> S getService(ServiceReference<S> reference) {
        return ((ServiceReferenceImpl<S>) reference).getService(bundle);
    }

    @Override
    public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean ungetService(ServiceReference<?> reference) {
        //        throw new UnsupportedOperationException();
        return true;
    }

    @Override
    public File getDataFile(String filename) {
        return bundle.getDataFile(filename);
    }

    @Override
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        return FrameworkUtil.createFilter(filter);
    }

    @Override
    public Bundle getBundle(String location) {
        if (Constants.SYSTEM_BUNDLE_LOCATION.equals(location)) {
            return getBundle(Constants.SYSTEM_BUNDLE_ID);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public BundleActivator getActivator() {
        return activator;
    }

    void destroy() {
        serviceRegistry.unregisterServices(bundle);
    }
}
