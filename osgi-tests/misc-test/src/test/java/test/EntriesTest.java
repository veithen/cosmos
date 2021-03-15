/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2020 Andreas Veithen
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

import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;
import com.google.common.truth.Truth;

@RunWith(CosmosRunner.class)
public class EntriesTest {
    @Inject private BundleContext bundleContext;

    @Test
    public void testFindEntries() {
        List<URL> urls =
                Collections.list(bundleContext.getBundle().findEntries("/entries", "*.dat", false));
        Truth.assertThat(urls).hasSize(2);
    }
}