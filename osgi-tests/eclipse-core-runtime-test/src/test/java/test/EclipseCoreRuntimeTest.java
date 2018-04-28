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
package test;

import static com.google.common.truth.Truth.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class EclipseCoreRuntimeTest {
    @Test
    public void test() throws Exception {
        Bundle bundle = FrameworkUtil.getBundle(Platform.class);
        assertThat(Platform.isRunning()).isFalse();
        bundle.start();
        assertThat(Platform.isRunning()).isTrue();
        assertThat(TestPlugin.getInstance().getStateLocation()).isNotNull();
    }
}