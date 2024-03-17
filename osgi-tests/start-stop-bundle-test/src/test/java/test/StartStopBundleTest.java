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
package test;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class StartStopBundleTest {
    @Inject private Provider<MyService> myService;

    @Test
    public void testActivatorCalled() throws Exception {
        Bundle bundle = FrameworkUtil.getBundle(StartStopBundleTest.class);
        assertThat(Activator.isActive()).isFalse();
        bundle.start();
        assertThat(Activator.isActive()).isTrue();
        bundle.stop();
        assertThat(Activator.isActive()).isFalse();
    }

    @Test
    public void testServicesAreUnregistered() throws Exception {
        Bundle bundle = FrameworkUtil.getBundle(StartStopBundleTest.class);
        assertThat(myService.get()).isNull();
        bundle.start();
        assertThat(myService.get()).isNotNull();
        bundle.stop();
        assertThat(myService.get()).isNull();
    }
}
