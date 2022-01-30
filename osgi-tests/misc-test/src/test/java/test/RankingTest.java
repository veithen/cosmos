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

import java.util.Hashtable;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class RankingTest {
    @Inject private BundleContext bundleContext;

    @Test
    public void test() {
        ServiceReference<MyService> serviceRef1 =
                bundleContext
                        .registerService(MyService.class, new MyServiceImpl(), null)
                        .getReference();

        Hashtable<String, Object> props2 = new Hashtable<>();
        props2.put(Constants.SERVICE_RANKING, 10);
        ServiceReference<MyService> serviceRef2 =
                bundleContext
                        .registerService(MyService.class, new MyServiceImpl(), props2)
                        .getReference();

        Hashtable<String, Object> props3 = new Hashtable<>();
        props3.put(Constants.SERVICE_RANKING, -10);
        ServiceReference<MyService> serviceRef3 =
                bundleContext
                        .registerService(MyService.class, new MyServiceImpl(), props3)
                        .getReference();

        ServiceReference<MyService> serviceRef4 =
                bundleContext
                        .registerService(MyService.class, new MyServiceImpl(), null)
                        .getReference();

        assertThat(serviceRef1.compareTo(serviceRef2)).isLessThan(0);
        assertThat(serviceRef1.compareTo(serviceRef3)).isGreaterThan(0);
        assertThat(serviceRef1.compareTo(serviceRef4)).isGreaterThan(0);

        assertThat(bundleContext.getServiceReference(MyService.class)).isEqualTo(serviceRef2);
    }
}
