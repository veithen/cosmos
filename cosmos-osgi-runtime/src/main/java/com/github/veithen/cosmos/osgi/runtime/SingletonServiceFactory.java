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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

final class SingletonServiceFactory<S> implements ServiceFactory<S> {
    private final S service;

    SingletonServiceFactory(S service) {
        this.service = service;
    }

    S getService() {
        return service;
    }

    @Override
    public S getService(Bundle bundle, ServiceRegistration<S> registration) {
        return service;
    }

    @Override
    public void ungetService(Bundle bundle, ServiceRegistration<S> registration, S service) {
        
    }
}
