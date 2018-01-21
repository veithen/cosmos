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
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.github.veithen.cosmos.osgi.runtime.Configuration;
import com.github.veithen.cosmos.osgi.runtime.Runtime;

public class CosmosLoadClassTest {
    @Test
    public void test() throws Exception {
        Runtime runtime = Runtime.getInstance(Configuration.newDefault().build());
        Bundle bundle1 = runtime.getBundle("bundle1");
        assertEquals(Bundle.STARTING, bundle1.getState());
        bundle1.loadClass("bundle1.Helper");
        assertEquals(Bundle.ACTIVE, bundle1.getState());
    }
}
