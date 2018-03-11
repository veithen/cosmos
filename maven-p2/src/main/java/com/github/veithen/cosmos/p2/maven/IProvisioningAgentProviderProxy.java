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
package com.github.veithen.cosmos.p2.maven;

import java.net.URI;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;

import com.github.veithen.cosmos.plexus.CosmosRuntimeProvider;

@Component(role=IProvisioningAgentProvider.class)
public class IProvisioningAgentProviderProxy implements IProvisioningAgentProvider, Initializable {
    private IProvisioningAgentProvider target;

    @Requirement
    private CosmosRuntimeProvider cosmosRuntimeProvider;

    @Override
    public void initialize() throws InitializationException {
        target = cosmosRuntimeProvider.getRuntime().getService(IProvisioningAgentProvider.class);
    }

    @Override
    public IProvisioningAgent createAgent(URI location) throws ProvisionException {
        return target.createAgent(location);
    }
}
