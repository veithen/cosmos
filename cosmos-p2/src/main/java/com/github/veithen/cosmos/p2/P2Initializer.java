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
package com.github.veithen.cosmos.p2;

import org.osgi.framework.BundleException;

import com.github.veithen.cosmos.osgi.runtime.CosmosException;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.RuntimeInitializer;

public class P2Initializer implements RuntimeInitializer {
    public static final RuntimeInitializer INSTANCE = new P2Initializer();

    private P2Initializer() {}

    @Override
    public void initializeRuntime(Runtime runtime) throws CosmosException, BundleException {
        // TODO: only set this when debugging is enabled
        runtime.setProperty("ds.loglevel", "4");
        runtime.getBundle("org.apache.felix.scr").start();
        // Don't create a default agent. Users should use IProvisioningAgentProvider to create agents.
        // See org.eclipse.equinox.internal.p2.core.Activator for property keys and values.
        runtime.setProperty("eclipse.p2.data.area", "@none");
    }
}
