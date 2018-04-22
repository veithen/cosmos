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
package com.github.veithen.cosmos.osgi.runtime;

import org.osgi.service.log.LogService;
import org.slf4j.Logger;

import com.github.veithen.cosmos.osgi.runtime.internal.InternalLogger;

final class InternalLoggerImpl implements InternalLogger {
    private final Logger logger;
    private final String prefix;

    InternalLoggerImpl(Logger logger, String prefix) {
        this.logger = logger;
        this.prefix = prefix;
    }

    @Override
    public void log(int level, String message, Throwable exception) {
        switch (level) {
            case LogService.LOG_DEBUG:
                if (logger.isDebugEnabled()) {
                    logger.debug(prefix + message, exception);
                }
                break;
            case LogService.LOG_INFO:
                if (logger.isInfoEnabled()) {
                    logger.info(prefix + message, exception);
                }
                break;
            case LogService.LOG_WARNING:
                if (logger.isWarnEnabled()) {
                    logger.warn(prefix + message, exception);
                }
                break;
            case LogService.LOG_ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(prefix + message, exception);
                }
                break;
        }
    }
}
