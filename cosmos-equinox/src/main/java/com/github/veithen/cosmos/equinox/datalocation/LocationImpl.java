/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2024 Andreas Veithen
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

public final class LocationImpl implements Location {
    private final Location parent;
    private final URL defaultValue;
    private final boolean readOnly;
    private URL location;

    public LocationImpl(Location parent, URL defaultValue, boolean readOnly) {
        this.parent = parent;
        this.defaultValue = defaultValue;
        this.readOnly = readOnly;
    }

    @Override
    public boolean allowsDefault() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getDefault() {
        return defaultValue;
    }

    @Override
    public Location getParentLocation() {
        return parent;
    }

    @Override
    public synchronized URL getURL() {
        if (location == null) {
            URL defaultLocation = getDefault();
            if (defaultLocation != null) {
                setURL(defaultLocation, false);
            }
        }
        return location;
    }

    @Override
    public synchronized boolean isSet() {
        return location != null;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public boolean setURL(URL value, boolean lock) throws IllegalStateException {
        try {
            return set(value, lock);
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public synchronized boolean set(URL value, boolean lock)
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
    public boolean set(URL value, boolean lock, String lockFilePath)
            throws IllegalStateException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean lock() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void release() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLocked() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Location createLocation(Location parent, URL defaultValue, boolean readonly) {
        return new LocationImpl(parent, defaultValue, readonly);
    }

    @Override
    public URL getDataArea(String path) throws IOException {
        throw new UnsupportedOperationException();
    }
}
