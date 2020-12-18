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
import org.slf4j.Logger;

final class LoggerImpl extends AbstractLogger {
    LoggerImpl(Bundle bundle, String name) {
        super(bundle, name);
    }

    @Override
    protected void formatAndLog(Logger logger, LogLevel level, String prefix, String format, Object[] arguments, Throwable t) {
        if (t != null) {
            Object[] newArguments = new Object[arguments.length+1];
            System.arraycopy(arguments, 0, newArguments, 0, arguments.length);
            newArguments[arguments.length] = t;
            arguments = newArguments;
        }
        level.log(logger, prefix + format, arguments);
    }
}
