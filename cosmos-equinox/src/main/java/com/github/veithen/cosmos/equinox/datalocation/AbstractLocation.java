/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2021 Andreas Veithen
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

import java.io.IOException;
import java.net.URL;

import org.eclipse.osgi.service.datalocation.Location;

abstract class AbstractLocation implements Location {
    private final Location parent;
    private final boolean readOnly;
    private URL location;

    AbstractLocation(Location parent, boolean readOnly) {
        this.parent = parent;
        this.readOnly = readOnly;
    }

    @Override
    public final boolean allowsDefault() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Location getParentLocation() {
        return parent;
    }

    @Override
    public final synchronized URL getURL() {
        if (location == null) {
            URL defaultLocation = getDefault();
            if (defaultLocation != null) {
                setURL(defaultLocation, false);
            }
        }
        return location;
    }

    @Override
    public final synchronized boolean isSet() {
        return location != null;
    }

    @Override
    public final boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public final boolean setURL(URL value, boolean lock) throws IllegalStateException {
        try {
            return set(value, lock);
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public final synchronized boolean set(URL value, boolean lock)
            throws IllegalStateException, IOException {
        if (lock) {
            throw new UnsupportedOperationException();
        }
        if (location != null) {
            throw new IllegalStateException();
        }
        location = value;
        return true;
    }

    @Override
    public final boolean set(URL value, boolean lock, String lockFilePath)
            throws IllegalStateException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean lock() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void release() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isLocked() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Location createLocation(Location parent, URL defaultValue, boolean readonly) {
        return new LocationImpl(parent, defaultValue, readonly);
    }

    @Override
    public final URL getDataArea(String path) throws IOException {
        throw new UnsupportedOperationException();
    }
}
