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
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Wire;

final class BundleWiringImpl implements BundleWiring {
    @Override
    public Bundle getBundle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCurrent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInUse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BundleCapability> getCapabilities(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BundleRequirement> getRequirements(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BundleWire> getProvidedWires(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BundleWire> getRequiredWires(String namespace) {
        return Collections.emptyList();
    }

    @Override
    public BundleRevision getRevision() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<URL> findEntries(String path, String filePattern, int options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> listResources(String path, String filePattern, int options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Capability> getResourceCapabilities(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Requirement> getResourceRequirements(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Wire> getProvidedResourceWires(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Wire> getRequiredResourceWires(String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BundleRevision getResource() {
        throw new UnsupportedOperationException();
    }
}
