package com.github.veithen.cosmos.osgi.runtime.equinox;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;
import org.osgi.framework.FrameworkEvent;

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;

final class FrameworkLogAdapter implements FrameworkLog {
    private final Logger logger;

    FrameworkLogAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(FrameworkEvent frameworkEvent) {
    }

    @Override
    public void log(FrameworkLogEntry logEntry) {
        switch (logEntry.getSeverity()) {
            case FrameworkLogEntry.OK:
                logger.debug(logEntry.getMessage(), logEntry.getThrowable());
                break;
            case FrameworkLogEntry.INFO:
                logger.info(logEntry.getMessage(), logEntry.getThrowable());
                break;
            case FrameworkLogEntry.CANCEL:
            case FrameworkLogEntry.WARNING:
                logger.warn(logEntry.getMessage(), logEntry.getThrowable());
                break;
            case FrameworkLogEntry.ERROR:
                logger.error(logEntry.getMessage(), logEntry.getThrowable());
        }
    }

    @Override
    public void setWriter(Writer newWriter, boolean append) {
    }

    @Override
    public void setFile(File newFile, boolean append) throws IOException {
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public void setConsoleLog(boolean consoleLog) {
    }

    @Override
    public void close() {
    }
}
