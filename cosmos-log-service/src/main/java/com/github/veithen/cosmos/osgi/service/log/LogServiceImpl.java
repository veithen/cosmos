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
package com.github.veithen.cosmos.osgi.service.log;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.FormatterLogger;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

final class LogServiceImpl implements LogService {
    private final Bundle bundle;
    private final LoggerImpl defaultLogger;

    LogServiceImpl(Bundle bundle) {
        this.bundle = bundle;
        defaultLogger = new LoggerImpl(bundle, "osgi");
    }

    @Override
    public Logger getLogger(String name) {
        return getLogger(bundle, name, Logger.class);
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        return getLogger(bundle, clazz.getName(), Logger.class);
    }

    @Override
    public <L extends Logger> L getLogger(String name, Class<L> loggerType) {
        return getLogger(bundle, name, loggerType);
    }

    @Override
    public <L extends Logger> L getLogger(Class<?> clazz, Class<L> loggerType) {
        return getLogger(bundle, clazz.getName(), loggerType);
    }

    @Override
    public <L extends Logger> L getLogger(Bundle bundle, String name, Class<L> loggerType) {
        Logger logger;
        if (loggerType == Logger.class) {
            logger = new LoggerImpl(bundle, name);
        } else if (loggerType == FormatterLogger.class) {
            logger = new FormatterLoggerImpl(bundle, name);
        } else {
            throw new IllegalArgumentException();
        }
        return loggerType.cast(logger);
    }

    @SuppressWarnings("deprecation")
    private LogLevel getLogLevel(int level) {
        switch (level) {
            case LogService.LOG_DEBUG:
                return LogLevel.DEBUG;
            case LogService.LOG_INFO:
                return LogLevel.INFO;
            case LogService.LOG_WARNING:
                return LogLevel.WARN;
            case LogService.LOG_ERROR:
                return LogLevel.ERROR;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void log(int level, String message) {
        defaultLogger.log(getLogLevel(level), message);
    }

    @Override
    public void log(int level, String message, Throwable exception) {
        defaultLogger.log(getLogLevel(level), message, exception);
    }

    @Override
    public void log(ServiceReference<?> sr, int level, String message) {
        defaultLogger.log(getLogLevel(level), message, sr);
    }

    @Override
    public void log(ServiceReference<?> sr, int level, String message, Throwable exception) {
        defaultLogger.log(getLogLevel(level), message, sr, exception);
    }
}
