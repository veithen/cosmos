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

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

abstract class ServiceReferenceImpl<S> implements ServiceReference<S> {
    private final Service<?> service;

    ServiceReferenceImpl(Service<?> service) {
        this.service = service;
    }

    @Override
    public final Object getProperty(String key) {
        return service.getProperty(key);
    }

    @Override
    public final String[] getPropertyKeys() {
        return service.getPropertyKeys();
    }

    @Override
    public final Bundle getBundle() {
        return service.getBundle();
    }

    @Override
    public final Bundle[] getUsingBundles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isAssignableTo(Bundle bundle, String className) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int compareTo(Object reference) {
        ServiceReferenceImpl<?> other = (ServiceReferenceImpl<?>) reference;
        if (other.service == service) {
            return 0;
        }
        int rankingCompare = Integer.compare(service.getRanking(), other.service.getRanking());
        if (rankingCompare == 0) {
            return Long.compare(other.service.getId(), service.getId());
        }
        return rankingCompare;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ServiceReferenceImpl<?>
                && ((ServiceReferenceImpl<?>) obj).service == service;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(service);
    }

    @Override
    public Dictionary<String, Object> getProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> A adapt(Class<A> type) {
        return null;
    }

    abstract S getService(AbstractBundle bundle);
}
