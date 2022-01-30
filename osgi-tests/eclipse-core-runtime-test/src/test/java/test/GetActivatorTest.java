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
package test;

import static com.google.common.truth.Truth.assertThat;

import java.lang.reflect.Method;

import org.eclipse.core.internal.preferences.legacy.InitLegacyPreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.FrameworkUtil;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class GetActivatorTest {
    @Test
    public void test() throws Exception {
        FrameworkUtil.getBundle(Platform.class).start();
        Method getActivatorMethod =
                InitLegacyPreferences.class.getDeclaredMethod("getActivator", String.class);
        getActivatorMethod.setAccessible(true);
        Plugin plugin =
                (Plugin)
                        getActivatorMethod.invoke(
                                new InitLegacyPreferences(), "eclipse-core-runtime-test");
        assertThat(plugin).isNotNull();
        assertThat(plugin).isSameInstanceAs(TestPlugin.getInstance());
    }
}
