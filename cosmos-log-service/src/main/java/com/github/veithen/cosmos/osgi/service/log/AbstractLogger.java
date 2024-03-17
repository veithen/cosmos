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
package com.github.veithen.cosmos.osgi.service.log;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerConsumer;
import org.slf4j.LoggerFactory;

abstract class AbstractLogger implements Logger {
    private final org.slf4j.Logger logger;
    private final String prefix;

    AbstractLogger(Bundle bundle, String name) {
        logger = LoggerFactory.getLogger(name);
        prefix = String.format("[%s] ", bundle.getSymbolicName());
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    private void log(LogLevel level, String message) {
        level.log(logger, prefix + message, (Throwable) null);
    }

    protected abstract void formatAndLog(
            org.slf4j.Logger logger,
            LogLevel level,
            String prefix,
            String format,
            Object[] arguments,
            Throwable t);

    private void internalLog(LogLevel level, String format, Object... arguments) {
        int removeArgs = 0;
        ServiceReference<?> sr = null;
        Throwable t = null;
        if (arguments.length > 0) {
            Object arg = arguments[arguments.length - 1];
            if (arg instanceof Throwable) {
                removeArgs++;
                t = (Throwable) arg;
                if (arguments.length > 1) {
                    arg = arguments[arguments.length - 2];
                    if (arg instanceof ServiceReference<?>) {
                        removeArgs++;
                        sr = (ServiceReference<?>) arg;
                    }
                }
            } else if (arg instanceof ServiceReference<?>) {
                removeArgs++;
                sr = (ServiceReference<?>) arg;
                if (arguments.length > 1) {
                    arg = arguments[arguments.length - 2];
                    if (arg instanceof Throwable) {
                        removeArgs++;
                        t = (Throwable) arg;
                    }
                }
            }
        }
        String prefix = this.prefix;
        if (sr != null) {
            prefix = prefix + "[" + sr.getProperty(Constants.SERVICE_ID) + "] ";
        }
        if (removeArgs == arguments.length) {
            level.log(logger, prefix + format, t);
        } else {
            if (removeArgs > 0) {
                Object[] newArguments = new Object[arguments.length - removeArgs];
                System.arraycopy(arguments, 0, newArguments, 0, newArguments.length);
                arguments = newArguments;
            }
            formatAndLog(logger, level, prefix, format, arguments, t);
        }
    }

    void log(LogLevel level, String format, Object... arguments) {
        if (level.isEnabled(logger)) {
            internalLog(level, format, arguments);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String message) {
        if (logger.isTraceEnabled()) {
            log(LogLevel.TRACE, message);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (logger.isTraceEnabled()) {
            internalLog(LogLevel.TRACE, format, arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (logger.isTraceEnabled()) {
            internalLog(LogLevel.TRACE, format, arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (logger.isTraceEnabled()) {
            internalLog(LogLevel.TRACE, format, arguments);
        }
    }

    @Override
    public <E extends Exception> void trace(LoggerConsumer<E> consumer) throws E {
        if (logger.isTraceEnabled()) {
            consumer.accept(this);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String message) {
        if (logger.isDebugEnabled()) {
            log(LogLevel.DEBUG, message);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (logger.isDebugEnabled()) {
            internalLog(LogLevel.DEBUG, format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (logger.isDebugEnabled()) {
            internalLog(LogLevel.DEBUG, format, arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (logger.isDebugEnabled()) {
            internalLog(LogLevel.DEBUG, format, arguments);
        }
    }

    @Override
    public <E extends Exception> void debug(LoggerConsumer<E> consumer) throws E {
        if (logger.isDebugEnabled()) {
            consumer.accept(this);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String message) {
        if (logger.isInfoEnabled()) {
            log(LogLevel.INFO, message);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (logger.isInfoEnabled()) {
            internalLog(LogLevel.INFO, format, arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (logger.isInfoEnabled()) {
            internalLog(LogLevel.INFO, format, arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (logger.isInfoEnabled()) {
            internalLog(LogLevel.INFO, format, arguments);
        }
    }

    @Override
    public <E extends Exception> void info(LoggerConsumer<E> consumer) throws E {
        if (logger.isInfoEnabled()) {
            consumer.accept(this);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String message) {
        if (logger.isWarnEnabled()) {
            log(LogLevel.WARN, message);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (logger.isWarnEnabled()) {
            internalLog(LogLevel.WARN, format, arg);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (logger.isWarnEnabled()) {
            internalLog(LogLevel.WARN, format, arg1, arg2);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (logger.isWarnEnabled()) {
            internalLog(LogLevel.WARN, format, arguments);
        }
    }

    @Override
    public <E extends Exception> void warn(LoggerConsumer<E> consumer) throws E {
        if (logger.isWarnEnabled()) {
            consumer.accept(this);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String message) {
        if (logger.isErrorEnabled()) {
            log(LogLevel.ERROR, message);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (logger.isErrorEnabled()) {
            internalLog(LogLevel.ERROR, format, arg);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (logger.isErrorEnabled()) {
            internalLog(LogLevel.ERROR, format, arg1, arg2);
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (logger.isErrorEnabled()) {
            internalLog(LogLevel.ERROR, format, arguments);
        }
    }

    @Override
    public <E extends Exception> void error(LoggerConsumer<E> consumer) throws E {
        if (logger.isErrorEnabled()) {
            consumer.accept(this);
        }
    }

    @Override
    public void audit(String message) {
        info(message);
    }

    @Override
    public void audit(String format, Object arg) {
        info(format, arg);
    }

    @Override
    public void audit(String format, Object arg1, Object arg2) {
        info(format, arg1, arg2);
    }

    @Override
    public void audit(String format, Object... arguments) {
        info(format, arguments);
    }
}
