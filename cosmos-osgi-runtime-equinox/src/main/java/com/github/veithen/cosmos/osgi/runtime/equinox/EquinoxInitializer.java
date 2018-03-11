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
package com.github.veithen.cosmos.osgi.runtime.equinox;

import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.internal.location.BasicLocation;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.github.veithen.cosmos.osgi.runtime.CosmosException;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.RuntimeInitializer;

public final class EquinoxInitializer implements RuntimeInitializer {
    public static final RuntimeInitializer INSTANCE = new EquinoxInitializer();

    private EquinoxInitializer() {}

    @Override
    public void initializeRuntime(Runtime runtime) throws CosmosException, BundleException {
        Bundle bundle = runtime.getBundle("org.eclipse.osgi");
        runtime.registerService(bundle, new String[] { Location.class.getName() }, new BasicLocation("dummy", null, false, null, null, null, null), null);
        runtime.registerService(bundle, new String[] { DebugOptions.class.getName() }, new DebugOptionsImpl(), null);
        runtime.registerService(bundle, new String[] { FrameworkLog.class.getName() }, new FrameworkLogAdapter(runtime.getLogger()), null);
    }
}
