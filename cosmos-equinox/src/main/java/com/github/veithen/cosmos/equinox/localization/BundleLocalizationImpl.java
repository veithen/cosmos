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
package com.github.veithen.cosmos.equinox.localization;

import java.util.ResourceBundle;

import org.eclipse.osgi.service.localization.BundleLocalization;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;

import com.github.veithen.cosmos.osgi.runtime.internal.InternalBundle;

@Component(service={BundleLocalization.class}, xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public class BundleLocalizationImpl implements BundleLocalization {
    @Override
    public ResourceBundle getLocalization(Bundle bundle, String locale) {
        return ((InternalBundle)bundle).getResourceBundle();
    }
}
