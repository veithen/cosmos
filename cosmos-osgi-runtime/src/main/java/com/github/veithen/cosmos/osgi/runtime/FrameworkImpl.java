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

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.resource.Requirement;

final class FrameworkImpl extends AbstractBundle implements Framework, FrameworkWiring {
    private static final Version VERSION = new Version(0, 0, 0);

    private final BundleManager bundleManager;

    FrameworkImpl(BundleManager bundleManager) {
        this.bundleManager = bundleManager;
    }

    @Override
    void initialize(BundleContextFactory bundleContextFactory) throws BundleException {
        context = bundleContextFactory.createBundleContext(this);
    }

    public long getBundleId() {
        return 0L;
    }

    public String getSymbolicName() {
        return Constants.SYSTEM_BUNDLE_SYMBOLICNAME;
    }

    @Override
    public Version getVersion() {
        return VERSION;
    }

    @Override
    public int getState() {
        return Bundle.ACTIVE;
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return null;
    }

    @Override
    String getHeaderValue(String name) throws BundleException {
        return null;
    }

    @Override
    public Dictionary<String, String> getHeaders() {
        return new Hashtable<>();
    }

    @Override
    public Dictionary<String, String> getHeaders(String locale) {
        return new Hashtable<>();
    }

    @Override
    void beforeLoadClass(String name) throws BundleException {}

    @Override
    public void init() throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(FrameworkListener... listeners) throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FrameworkEvent waitForStop(long timeout) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start(int options) throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop() throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop(int options) throws BundleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocation() {
        return Constants.SYSTEM_BUNDLE_LOCATION;
    }

    @Override
    public URL getEntry(String path) {
        return null;
    }

    @Override
    public Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
        return null;
    }

    @Override
    public void refreshBundles(Collection<Bundle> bundles, FrameworkListener... listeners) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean resolveBundles(Collection<Bundle> bundles) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Bundle> getRemovalPendingBundles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Bundle> getDependencyClosure(Collection<Bundle> bundles) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<BundleCapability> findProviders(Requirement requirement) {
        return bundleManager.findProviders(requirement);
    }

    @Override
    List<BundleCapability> getDeclaredCapabilities(String namespace) {
        return Collections.emptyList();
    }
}
