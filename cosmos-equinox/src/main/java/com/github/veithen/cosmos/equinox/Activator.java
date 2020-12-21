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
package com.github.veithen.cosmos.equinox;

import org.eclipse.osgi.signedcontent.SignedContentFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.github.veithen.cosmos.equinox.signedcontent.DummySignedContentFactory;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        if ("true".equals(context.getProperty("cosmos.equinox.disableSignatureValidation"))) {
            context.registerService(SignedContentFactory.class, new DummySignedContentFactory(), null);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
