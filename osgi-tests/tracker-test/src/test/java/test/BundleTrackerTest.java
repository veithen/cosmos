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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class BundleTrackerTest {
    @Inject private BundleContext bundleContext;

    @Test
    public void testBasic() throws Exception {
        // Use a random bundle for the test.
        Bundle bundle = FrameworkUtil.getBundle(Logger.class);
        BundleTracker<Bundle> tracker = new BundleTracker<>(bundleContext, Bundle.ACTIVE, null);
        tracker.open();
        assertThat(tracker.getBundles()).doesNotContain(bundle);
        bundle.start();
        assertThat(tracker.getBundles()).contains(bundle);
        bundle.stop();
        assertThat(tracker.getBundles()).doesNotContain(bundle);
    }

    @Test
    public void testLazyActivationWhileAdding() {
        final List<Integer> states = new ArrayList<>();
        BundleTracker<Bundle> tracker =
                new BundleTracker<>(
                        bundleContext,
                        Bundle.STARTING | Bundle.ACTIVE,
                        new BundleTrackerCustomizer<Bundle>() {
                            @Override
                            public Bundle addingBundle(Bundle bundle, BundleEvent event) {
                                if (bundle.getSymbolicName().equals("bundle1")) {
                                    states.add(bundle.getState());
                                    try {
                                        // Load a class from the bundle to cause it to activate.
                                        bundle.loadClass("bundle1.Helper");
                                        return bundle;
                                    } catch (ClassNotFoundException ex) {
                                        // Ignore. Test will fail later.
                                    }
                                }
                                return null;
                            }

                            @Override
                            public void modifiedBundle(
                                    Bundle bundle, BundleEvent event, Bundle object) {}

                            @Override
                            public void removedBundle(
                                    Bundle bundle, BundleEvent event, Bundle object) {}
                        });
        tracker.open();
        // The customizer should have been called once, with the bundle in state STARTING.
        assertThat(states).containsExactly(Bundle.STARTING);
        assertThat(tracker.getTracked()).hasSize(1);
        tracker.close();
    }
}
