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
package com.github.veithen.cosmos.equinox.debug;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugTrace;
import org.osgi.service.component.annotations.Component;

@Component(
        service = {DebugOptions.class},
        xmlns = "http://www.osgi.org/xmlns/scr/v1.1.0")
public final class DebugOptionsImpl implements DebugOptions {
    private final Map<String, String> options = new HashMap<String, String>();
    private boolean debugEnabled;

    @Override
    public boolean getBooleanOption(String option, boolean defaultValue) {
        String value = getOption(option);
        return value == null ? defaultValue : value.equalsIgnoreCase("true");
    }

    @Override
    public String getOption(String option) {
        return getOption(option, null);
    }

    @Override
    public String getOption(String option, String defaultValue) {
        synchronized (options) {
            return options.containsKey(option) ? options.get(option) : defaultValue;
        }
    }

    @Override
    public int getIntegerOption(String option, int defaultValue) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getOptions() {
        synchronized (options) {
            return new HashMap<String, String>(options);
        }
    }

    @Override
    public void setOption(String option, String value) {
        synchronized (options) {
            options.put(option, value);
        }
    }

    @Override
    public void setOptions(Map<String, String> options) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeOption(String option) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean isDebugEnabled() {
        return debugEnabled;
    }

    @Override
    public synchronized void setDebugEnabled(boolean value) {
        this.debugEnabled = value;
    }

    @Override
    public void setFile(File newFile) {
        // TODO
        // throw new UnsupportedOperationException();
    }

    @Override
    public File getFile() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public DebugTrace newDebugTrace(String bundleSymbolicName) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public DebugTrace newDebugTrace(String bundleSymbolicName, Class<?> traceEntryClass) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
