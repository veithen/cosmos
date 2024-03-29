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

import java.util.Hashtable;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class ServiceTrackerTest {
    @Inject private BundleContext bundleContext;

    @Test
    public void testRegisterUnregisterService() throws Exception {
        ServiceTracker<MyService, MyService> tracker =
                new ServiceTracker<>(bundleContext, MyService.class, null);
        tracker.open();
        assertThat(tracker.getService()).isNull();
        MyServiceImpl service = new MyServiceImpl();
        ServiceRegistration<MyService> registration =
                bundleContext.registerService(MyService.class, service, null);
        assertThat(tracker.getService()).isSameAs(service);
        registration.unregister();
        assertThat(tracker.getService()).isNull();
    }

    @Test
    public void testServicePropertyChange() throws Exception {
        MyServiceImpl service = new MyServiceImpl();
        ServiceRegistration<MyService> registration =
                bundleContext.registerService(MyService.class, service, null);
        ServiceTracker<MyService, MyService> tracker =
                new ServiceTracker<>(
                        bundleContext,
                        bundleContext.createFilter(
                                String.format(
                                        "(&(objectClass=%s)(myproperty=foobar))",
                                        MyService.class.getName())),
                        null);
        tracker.open();
        assertThat(tracker.getService()).isNull();
        Hashtable<String, String> newProperties = new Hashtable<>();
        newProperties.put("myproperty", "foobar");
        registration.setProperties(newProperties);
        assertThat(tracker.getService()).isSameAs(service);
    }
}
