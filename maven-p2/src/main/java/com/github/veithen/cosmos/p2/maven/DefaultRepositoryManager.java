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

import java.io.File;
import java.net.URI;
import java.util.Hashtable;

import org.apache.maven.artifact.manager.WagonManager;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.osgi.framework.Constants;

import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

@Component(role=RepositoryManager.class)
public class DefaultRepositoryManager implements RepositoryManager, Initializable, Disposable {
    private IArtifactRepositoryManager repoman;
    
    @Requirement
    private WagonManager wagonManager;
    
    public void initialize() throws InitializationException {
        try {
            Runtime runtime = Runtime.getInstance();
            
            System.out.println("Setting up proxy configuration");
            Hashtable<String,Object> properties = new Hashtable<String,Object>();
            properties.put(Constants.SERVICE_RANKING, Integer.valueOf(1));
            runtime.registerService(new String[] { IProxyService.class.getName() }, new ProxyServiceAdapter(wagonManager), properties);
            
            IProvisioningAgent agent = runtime.getService(IProvisioningAgentProvider.class).createAgent(new File("target/p2-data").toURI());
            repoman = (IArtifactRepositoryManager)agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
        } catch (Exception ex) {
            throw new InitializationException("Failed to initialize P2", ex);
        }
    }

    public IArtifactRepository loadRepository(URI uri) throws ProvisionException {
        System.out.println("Loading repository " + uri);
        return repoman.loadRepository(uri, new SystemOutProgressMonitor());
    }

    public void dispose() {
        
    }
}
