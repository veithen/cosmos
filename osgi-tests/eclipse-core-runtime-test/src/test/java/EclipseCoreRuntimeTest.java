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
import static com.google.common.truth.Truth.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.github.veithen.cosmos.osgi.runtime.Runtime;

public class EclipseCoreRuntimeTest {
    @Test
    public void test() throws Exception {
        Bundle bundle = Runtime.getInstance().getBundle("org.eclipse.core.runtime");
        bundle.start();
        assertThat(Platform.isRunning()).isTrue();
    }
}
