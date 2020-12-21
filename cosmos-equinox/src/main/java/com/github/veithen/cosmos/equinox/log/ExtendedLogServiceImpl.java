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

import org.eclipse.equinox.log.ExtendedLogService;
import org.eclipse.equinox.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.LoggerConsumer;

@Component(service={ExtendedLogService.class}, xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public final class ExtendedLogServiceImpl implements ExtendedLogService {
    private LogService logService;

    @Reference
    void bindLogService(LogService logService) {
        this.logService = logService;
    }

    @Override
    public void log(int level, String message) {
        logService.log(level, message);
    }

    @Override
    public void log(int level, String message, Throwable exception) {
        logService.log(level, message, exception);
    }

    @Override
    public void log(ServiceReference sr, int level, String message) {
        logService.log(sr, level, message);
    }

    @Override
    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        logService.log(sr, level, message, exception);
    }

    @Override
    public void log(Object context, int level, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log(Object context, int level, String message, Throwable exception) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLoggable(int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getLogger(String loggerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getLogger(Bundle bundle, String loggerName) {
        return new LoggerAdapter(logService.getLogger(bundle, loggerName, org.osgi.service.log.Logger.class));
    }

    @Override
    public org.osgi.service.log.Logger getLogger(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <L extends org.osgi.service.log.Logger> L getLogger(String name, Class<L> loggerType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <L extends org.osgi.service.log.Logger> L getLogger(Class<?> clazz,
            Class<L> loggerType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <L extends org.osgi.service.log.Logger> L getLogger(Bundle bundle, String name,
            Class<L> loggerType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTraceEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Exception> void trace(LoggerConsumer<E> consumer) throws E {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDebugEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Exception> void debug(LoggerConsumer<E> consumer) throws E {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInfoEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Exception> void info(LoggerConsumer<E> consumer) throws E {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWarnEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Exception> void warn(LoggerConsumer<E> consumer) throws E {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isErrorEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Exception> void error(LoggerConsumer<E> consumer) throws E {
        throw new UnsupportedOperationException();
    }

    @Override
    public void audit(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void audit(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void audit(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void audit(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }
}
