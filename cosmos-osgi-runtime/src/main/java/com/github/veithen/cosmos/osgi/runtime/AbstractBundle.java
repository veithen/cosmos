/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2021 Andreas Veithen
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleCapability;

import com.github.veithen.cosmos.osgi.runtime.internal.InternalBundle;

abstract class AbstractBundle implements InternalBundle, BundleReference {
    protected BundleContextImpl context;

    abstract void initialize(BundleContextFactory bundleContextFactory) throws BundleException;

    abstract String getHeaderValue(String name) throws BundleException;

    public final BundleContext getBundleContext() {
        return context;
    }

    public final int compareTo(Bundle o) {
        throw new UnsupportedOperationException();
    }

    public final void update(InputStream input) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public final void update() throws BundleException {
        throw new UnsupportedOperationException();
    }

    public final void uninstall() throws BundleException {
        throw new UnsupportedOperationException();
    }

    public final Enumeration<String> getEntryPaths(String path) {
        throw new UnsupportedOperationException();
    }

    public final ServiceReference<?>[] getRegisteredServices() {
        throw new UnsupportedOperationException();
    }

    public final ServiceReference<?>[] getServicesInUse() {
        throw new UnsupportedOperationException();
    }

    public final boolean hasPermission(Object permission) {
        return true;
    }

    public final URL getResource(String name) {
        throw new UnsupportedOperationException();
    }

    public final Enumeration<URL> getResources(String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    public final long getLastModified() {
        // We never modify bundles, so we may as well return 0 here
        return 0;
    }

    public final Map<X509Certificate, List<X509Certificate>> getSignerCertificates(
            int signersType) {
        throw new UnsupportedOperationException();
    }

    public <A> A adapt(Class<A> type) {
        if (type.isInstance(this)) {
            return type.cast(this);
        } else {
            return null;
        }
    }

    public final File getDataFile(String filename) {
        // We don't have filesystem support.
        return null;
    }

    public final Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            beforeLoadClass(name);
        } catch (BundleException ex) {
            throw new ClassNotFoundException(name, ex);
        }
        return Class.forName(name);
    }

    abstract void beforeLoadClass(String name) throws BundleException;

    void distributeBundleEvent(BundleEvent event) {
        if (context != null) {
            context.distributeBundleEvent(event);
        }
    }

    abstract List<BundleCapability> getDeclaredCapabilities(String namespace);

    @Override
    public final Bundle getBundle() {
        return this;
    }
}
