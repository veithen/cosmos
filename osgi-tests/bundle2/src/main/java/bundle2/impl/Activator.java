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
package bundle2.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import bundle2.MyService;

public class Activator implements BundleActivator {
    public void start(BundleContext context) throws Exception {
        context.registerService(MyService.class.getName(), new MyServiceImpl(), null);
    }

    public void stop(BundleContext context) throws Exception {}
}
