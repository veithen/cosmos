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

import org.slf4j.Logger;

abstract class LogLevel {
    static final LogLevel TRACE = new LogLevel() {
        @Override
        boolean isEnabled(Logger logger) {
            return logger.isTraceEnabled();
        }

        @Override
        void log(Logger logger, String msg, Throwable t) {
            logger.trace(msg, t);
        }

        @Override
        void log(Logger logger, String format, Object... arguments) {
            logger.trace(format, arguments);
        }
    };

    static final LogLevel DEBUG = new LogLevel() {
        @Override
        boolean isEnabled(Logger logger) {
            return logger.isDebugEnabled();
        }

        @Override
        void log(Logger logger, String msg, Throwable t) {
            logger.debug(msg, t);
        }

        @Override
        void log(Logger logger, String format, Object... arguments) {
            logger.debug(format, arguments);
        }
    };

    static final LogLevel INFO = new LogLevel() {
        @Override
        boolean isEnabled(Logger logger) {
            return logger.isInfoEnabled();
        }

        @Override
        void log(Logger logger, String msg, Throwable t) {
            logger.info(msg, t);
        }

        @Override
        void log(Logger logger, String format, Object... arguments) {
            logger.info(format, arguments);
        }
    };

    static final LogLevel WARN = new LogLevel() {
        @Override
        boolean isEnabled(Logger logger) {
            return logger.isWarnEnabled();
        }

        @Override
        void log(Logger logger, String msg, Throwable t) {
            logger.warn(msg, t);
        }

        @Override
        void log(Logger logger, String format, Object... arguments) {
            logger.warn(format, arguments);
        }
    };

    static final LogLevel ERROR = new LogLevel() {
        @Override
        boolean isEnabled(Logger logger) {
            return logger.isErrorEnabled();
        }

        @Override
        void log(Logger logger, String msg, Throwable t) {
            logger.error(msg, t);
        }

        @Override
        void log(Logger logger, String format, Object... arguments) {
            logger.error(format, arguments);
        }
    };

    private LogLevel() {}

    abstract boolean isEnabled(Logger logger);
    abstract void log(Logger logger, String msg, Throwable t);
    abstract void log(Logger logger, String format, Object... arguments);
}
