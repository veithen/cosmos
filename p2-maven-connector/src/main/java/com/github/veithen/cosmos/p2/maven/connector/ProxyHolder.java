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

import org.eclipse.aether.repository.Proxy;

/**
 * Plexus component holding the currently configured proxy.
 * <p>
 * The P2 code only supports one globally configured proxy. This component holds the reference to
 * the proxy that is currently configured. It is designed to allow two threads to proceed
 * concurrently if they are using the same proxy.
 */
public interface ProxyHolder {
    public interface Lease extends AutoCloseable {
        void close();
    }

    Lease withProxy(Proxy proxy) throws InterruptedException;
    Proxy getCurrentProxy();
}
