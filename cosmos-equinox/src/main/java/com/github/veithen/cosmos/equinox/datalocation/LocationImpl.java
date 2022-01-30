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
package com.github.veithen.cosmos.equinox.datalocation;

import java.net.URL;

import org.eclipse.osgi.service.datalocation.Location;

final class LocationImpl extends AbstractLocation {
    private final URL defaultValue;

    LocationImpl(Location parent, URL defaultValue, boolean readOnly) {
        super(parent, readOnly);
        this.defaultValue = defaultValue;
    }

    @Override
    public URL getDefault() {
        return defaultValue;
    }
}
