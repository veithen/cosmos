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

import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ServiceContext<S> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceContext.class);

    private final Service<S> service;
    private final AbstractBundle bundle;
    private int refCount;
    private S serviceObject;

    ServiceContext(Service<S> service, AbstractBundle bundle) {
        this.service = service;
        this.bundle = bundle;
    }

    S getService() {
        if (bundle != null && logger.isDebugEnabled()) {
            logger.debug(
                    "Bundle "
                            + bundle.getSymbolicName()
                            + " is getting service "
                            + service.getProperty(Constants.SERVICE_ID));
        }
        if (serviceObject == null) {
            serviceObject = service.getServiceFactory().getService(bundle, service);
        }
        refCount++;
        return serviceObject;
    }
}
