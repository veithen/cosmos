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
package com.github.veithen.cosmos.equinox.log;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service={FrameworkLog.class}, xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public final class FrameworkLogAdapter implements FrameworkLog {
    private static final Logger logger = LoggerFactory.getLogger(FrameworkLog.class);

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
