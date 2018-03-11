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
package com.github.veithen.cosmos.osgi.runtime.equinox;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;
import org.osgi.framework.FrameworkEvent;

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;

public final class FrameworkLogAdapter implements FrameworkLog {
    private Logger logger;

    protected void bindLogger(Logger logger) {
        this.logger = logger;
    }

    protected void unbindLogger() {
        this.logger = null;
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
