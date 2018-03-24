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
package com.github.veithen.cosmos.p2.maven.connector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.osgi.framework.BundleException;

import com.github.veithen.cosmos.osgi.runtime.CosmosRuntime;

@Component(role=RepositoryConnectorFactory.class, hint="p2")
public final class P2RepositoryConnectorFactory implements RepositoryConnectorFactory, LogEnabled {
    private Logger logger;
    
    private final Map<File,IProvisioningAgent> provisioningAgents = new HashMap<>();
    
    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public float getPriority() {
        return 0;
    }

    @Override
    public RepositoryConnector newInstance(RepositorySystemSession session,
            RemoteRepository repository) throws NoRepositoryConnectorException {
        if (repository.getContentType().equals("p2")) {
            File localRepositoryDir = session.getLocalRepository().getBasedir();
            IProvisioningAgent provisioningAgent = provisioningAgents.get(localRepositoryDir);
            if (provisioningAgent == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Creating new provisioning agent for local repository %s", localRepositoryDir));
                }
                try {
                    provisioningAgent = CosmosRuntime.getInstance().getService(IProvisioningAgentProvider.class).createAgent(new File(localRepositoryDir, ".p2-metadata").toURI());
                } catch (BundleException | ProvisionException ex) {
                    logger.error(String.format("Failed to create provisioning agent for local repository %s", localRepositoryDir));
                    throw new NoRepositoryConnectorException(repository, ex);
                }
                provisioningAgents.put(localRepositoryDir, provisioningAgent);
            }
            return new P2RepositoryConnector(repository,
                    (IArtifactRepositoryManager)provisioningAgent.getService(IArtifactRepositoryManager.SERVICE_NAME),
                    logger);
        } else {
            throw new NoRepositoryConnectorException(repository);
        }
    }
}
