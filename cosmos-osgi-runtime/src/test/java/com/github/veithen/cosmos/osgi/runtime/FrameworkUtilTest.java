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
package com.github.veithen.cosmos.osgi.runtime;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public class FrameworkUtilTest {
    @Test
    public void testGetBundle() throws Exception {
        Runtime.getInstance(Configuration.newDefault().build());
        Bundle bundle = FrameworkUtil.getBundle(Bundle.class);
        assertThat(bundle).isNotNull();
        assertThat(bundle.getSymbolicName()).isEqualTo("osgi.core");
        Version version = bundle.getVersion();
        assertThat(version.getMajor()).isEqualTo(6);
    }
}
