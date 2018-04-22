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

    InternalLoggerImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(int level, String message, Throwable exception) {
        switch (level) {
            case LogService.LOG_DEBUG:
                logger.debug(message, exception);
                break;
            case LogService.LOG_INFO:
                logger.info(message, exception);
                break;
            case LogService.LOG_WARNING:
                logger.warn(message, exception);
                break;
            case LogService.LOG_ERROR:
                logger.error(message, exception);
                break;
        }
    }
}
