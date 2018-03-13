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
package com.github.veithen.cosmos.plexus;

import static com.google.common.truth.Truth.assertThat;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.LoggerManager;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.github.veithen.cosmos.osgi.runtime.Runtime;

public class PlexusTest {
    @Test
    public void test() throws Exception {
        PlexusContainer container = new DefaultPlexusContainer();
        try {
            container.lookup(LoggerManager.class).setThreshold(0);
            Runtime runtime = container.lookup(CosmosRuntimeProvider.class).getRuntime();
            assertThat(runtime).isNotNull();
            Bundle bundle = runtime.getBundle("test");
            assertThat(bundle).isNotNull();
            bundle.start();
            assertThat(Activator.getPlexusContainer()).isSameAs(container);
        } finally {
            container.dispose();
        }
    }
}
