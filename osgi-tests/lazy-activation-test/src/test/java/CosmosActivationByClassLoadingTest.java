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
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import bundle2.MyService;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class CosmosActivationByClassLoadingTest {
    @Inject private Provider<MyService> myService;

    @Test
    public void test() throws Exception {
        Bundle bundle2 = FrameworkUtil.getBundle(MyService.class);
        bundle2.start();
        assertEquals("bar", myService.get().getProperty("foo"));
    }
}
