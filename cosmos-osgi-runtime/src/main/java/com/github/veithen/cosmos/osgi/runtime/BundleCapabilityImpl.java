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

import java.util.Map;

import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

final class BundleCapabilityImpl implements BundleCapability {
    private final BundleImpl bundle;
    private final String namespace;
    private final Map<String, String> directives;
    private final Map<String, Object> attributes;

    public BundleCapabilityImpl(
            BundleImpl bundle,
            String namespace,
            Map<String, String> directives,
            Map<String, Object> attributes) {
        this.bundle = bundle;
        this.namespace = namespace;
        this.directives = directives;
        this.attributes = attributes;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public BundleRevision getRevision() {
        return bundle;
    }

    @Override
    public Map<String, String> getDirectives() {
        return directives;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public BundleRevision getResource() {
        return bundle;
    }
}
