package com.github.veithen.cosmos.osgi.runtime.equinox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugTrace;

final class DebugOptionsImpl implements DebugOptions {
    private final Map<String,String> options = new HashMap<String,String>();
    
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
    public Map getOptions() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOption(String option, String value) {
        synchronized (options) {
            options.put(option, value);
        }
    }

    @Override
    public void setOptions(Map options) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeOption(String option) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDebugEnabled() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDebugEnabled(boolean value) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFile(File newFile) {
        // TODO
        throw new UnsupportedOperationException();
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
    public DebugTrace newDebugTrace(String bundleSymbolicName, Class traceEntryClass) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
