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
package com.github.veithen.cosmos.plexus;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.github.veithen.cosmos.osgi.runtime.Runtime;

@Component(role=CosmosRuntimeProvider.class)
public class DefaultCosmosRuntimeProvider implements CosmosRuntimeProvider, Initializable, LogEnabled {
    @Requirement
    private PlexusContainer plexusContainer;
    
    private Logger logger;
    private Runtime runtime;
    
    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public void initialize() throws InitializationException {
        try {
            runtime = Runtime.getInstance(new PlexusLogger(logger));
            runtime.registerService(new String[] { PlexusContainer.class.getName() }, plexusContainer, null);
        } catch (Exception ex) {
            throw new InitializationException("Failed to initialize Cosmos OSGi runtime", ex);
        }
    }

    @Override
    public Runtime getRuntime() {
        return runtime;
    }
}
