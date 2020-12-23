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

import java.util.Collections;
import java.util.Enumeration;

import org.eclipse.equinox.log.ExtendedLogReaderService;
import org.eclipse.equinox.log.LogFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

@Component(
        service = {ExtendedLogReaderService.class},
        xmlns = "http://www.osgi.org/xmlns/scr/v1.1.0")
public final class ExtendedLogReaderServiceImpl implements ExtendedLogReaderService {
    @Override
    public void addLogListener(LogListener listener) {}

    @Override
    public void addLogListener(LogListener listener, LogFilter filter) {}

    @Override
    public void removeLogListener(LogListener listener) {}

    @Override
    public Enumeration<LogEntry> getLog() {
        return Collections.emptyEnumeration();
    }
}
