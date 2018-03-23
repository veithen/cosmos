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

import org.eclipse.aether.repository.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the currently configured proxy.
 * <p>
 * The P2 code only supports one globally configured proxy. This component holds the reference to
 * the proxy that is currently configured. It is designed to allow two threads to proceed
 * concurrently if they are using the same proxy.
 */
public final class ProxyHolder {
    public interface Lease extends AutoCloseable {
        void close();
    }

    private static final Logger logger = LoggerFactory.getLogger(ProxyHolder.class);

    private static final Object lock = new Object();
    private static Proxy currentProxy;
    private static List<Lease> leases = new LinkedList<>();

    private ProxyHolder() {}

    public static Lease withProxy(Proxy proxy) throws InterruptedException {
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
    
    public static Proxy getCurrentProxy() {
        synchronized (lock) {
            return currentProxy;
        }
    }
}
