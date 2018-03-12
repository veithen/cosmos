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
package com.github.veithen.cosmos.p2.maven.connector;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.aether.repository.Proxy;

@Component(role=ProxyHolder.class)
public class DefaultProxyHolder implements ProxyHolder, LogEnabled {
    private final Object lock = new Object();
    private Proxy currentProxy;
    private List<Lease> leases = new LinkedList<>();
    private Logger logger;

    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public Lease withProxy(Proxy proxy) throws InterruptedException {
        synchronized (lock) {
            while (true) {
                boolean proxySet = !leases.isEmpty();
                if (!proxySet || currentProxy == proxy) {
                    if (proxySet) {
                        logger.debug("Proxy already set");
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Setting proxy %s", proxy));
                        }
                        currentProxy = proxy;
                    }
                    Lease lease = new Lease() {
                        @Override
                        public void close() {
                            synchronized (lock) {
                                if (!leases.remove(this)) {
                                    throw new IllegalStateException();
                                }
                                if (leases.isEmpty()) {
                                    logger.debug("Unsetting proxy");
                                    currentProxy = null;
                                }
                                lock.notifyAll();
                            }
                        }
                    };
                    leases.add(lease);
                    return lease;
                }
                logger.debug("Another proxy currently set; wait");
                lock.wait();
            }
        }
    }
    
    public Proxy getCurrentProxy() {
        synchronized (lock) {
            return currentProxy;
        }
    }
}
