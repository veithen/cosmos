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
package com.github.veithen.cosmos.equinox.datalocation;

import java.io.IOException;
import java.net.URL;

import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.service.component.annotations.Component;

@Component(service={Location.class}, xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public final class AnyLocation implements Location {
    @Override
    public Location createLocation(Location parent, URL defaultValue, boolean readonly) {
        return new LocationImpl(parent, defaultValue, readonly);
    }

    @Override
    public boolean allowsDefault() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getDefault() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Location getParentLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setURL(URL value, boolean lock) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean set(URL value, boolean lock) throws IllegalStateException, IOException {
        throw new UnsupportedOperationException();
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
    public URL getDataArea(String path) throws IOException {
        throw new UnsupportedOperationException();
    }
}
