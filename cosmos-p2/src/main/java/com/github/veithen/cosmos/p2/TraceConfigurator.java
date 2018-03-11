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
package com.github.veithen.cosmos.p2;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;

@Component(immediate=true, xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public final class TraceConfigurator {
    private static final String[] optionsForTrace = {
        "org.eclipse.equinox.p2.core/debug",
        "org.eclipse.equinox.p2.core/generator/parsing",
        "org.eclipse.equinox.p2.core/engine/installregistry",
        "org.eclipse.equinox.p2.core/metadata/parsing",
        "org.eclipse.equinox.p2.core/artifacts/mirrors",
        "org.eclipse.equinox.p2.core/core/parseproblems",
        "org.eclipse.equinox.p2.core/planner/operands",
        "org.eclipse.equinox.p2.core/planner/projector",
        "org.eclipse.equinox.p2.core/engine/profilepreferences",
        "org.eclipse.equinox.p2.core/publisher",
        "org.eclipse.equinox.p2.core/reconciler",
        "org.eclipse.equinox.p2.core/core/removeRepo",
        "org.eclipse.equinox.p2.core/updatechecker",
        "org.eclipse.equinox.p2.repository/credentials/debug",
        "org.eclipse.equinox.p2.repository/transport/debug",
        "org.eclipse.ecf/debug",
        "org.eclipse.ecf/debug/exceptions/catching",
        "org.eclipse.ecf/debug/exceptions/throwing",
        "org.eclipse.ecf/debug/methods/entering",
        "org.eclipse.ecf/debug/methods/exiting",
        "org.eclipse.ecf.filetransfer/debug",
        "org.eclipse.ecf.filetransfer/debug/exceptions/throwing",
        "org.eclipse.ecf.filetransfer/debug/exceptions/catching",
        "org.eclipse.ecf.filetransfer/debug/methods/entering",
        "org.eclipse.ecf.filetransfer/debug/methods/exiting",
        "org.eclipse.ecf.provider.filetransfer/debug",
        "org.eclipse.ecf.provider.filetransfer/debug/exceptions/catching",
        "org.eclipse.ecf.provider.filetransfer/debug/exceptions/throwing",
        "org.eclipse.ecf.provider.filetransfer/debug/methods/entering",
        "org.eclipse.ecf.provider.filetransfer/debug/methods/exiting",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/exceptions/catching",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/exceptions/throwing",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/methods/entering",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/methods/exiting",
    };
    
    private Logger logger;
    private DebugOptions debugOptions;
    
    @Reference
    private void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    @Reference
    private void setDebugOptions(DebugOptions debugOptions) {
        this.debugOptions = debugOptions;
    }
    
    @Activate
    private void activate() {
        if (logger.isDebugEnabled()) {
            for (String option : optionsForTrace) {
                debugOptions.setOption(option, "true");
            }
        }
    }
}
