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
package com.github.veithen.cosmos.osgi.runtime;

import java.util.Optional;

import org.osgi.framework.Bundle;
import org.osgi.framework.connect.FrameworkUtilHelper;

import com.github.veithen.cosmos.osgi.runtime.internal.BundleLookup;

public final class FrameworkUtilHelperImpl implements FrameworkUtilHelper {
    private static BundleLookup bundleLookup;

    static void setBundleLookup(BundleLookup bundleLookup) {
        FrameworkUtilHelperImpl.bundleLookup = bundleLookup;
    }

    @Override
    public Optional<Bundle> getBundle(Class<?> classFromBundle) {
        return Optional.ofNullable(bundleLookup.getBundle(classFromBundle));
    }
}
