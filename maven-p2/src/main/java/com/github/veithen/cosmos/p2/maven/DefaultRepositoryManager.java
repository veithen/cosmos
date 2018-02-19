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
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.osgi.framework.Constants;

import com.github.veithen.cosmos.osgi.runtime.Configuration;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.logging.plexus.PlexusLogger;
import com.github.veithen.cosmos.p2.P2Initializer;
import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

@Component(role=RepositoryManager.class)
public class DefaultRepositoryManager implements RepositoryManager, Initializable, Disposable, LogEnabled {
    private IArtifactRepositoryManager repoman;
    
    @Requirement
    private WagonManager wagonManager;
    
    private Logger logger;
    
    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public void initialize() throws InitializationException {
        try {
            Runtime runtime = Runtime.getInstance(Configuration.newDefault().logger(new PlexusLogger(logger)).initializer(new P2Initializer(new File("target/p2-data"), logger == null || logger.isDebugEnabled())).build());
            
            System.out.println("Setting up proxy configuration");
            Hashtable<String,Object> properties = new Hashtable<String,Object>();
            properties.put(Constants.SERVICE_RANKING, Integer.valueOf(1));
            runtime.registerService(new String[] { IProxyService.class.getName() }, new ProxyServiceAdapter(wagonManager), properties);
            
            // Don't let P2 use mirrors. There are two reasons for this:
            // 1) Mirrors may occasionally be unreachable. Using them would make the build less stable and predictable.
            // 2) P2 may use a mirror that uses a different protocol, typically FTP instead of HTTP. This is
            //    a problem when building from behind a proxy: since Maven repositories almost exclusively use
            //    HTTP, users generally don't have a correct FTP proxy configuration in settings.xml. Allowing P2
            //    to switch to FTP would then cause occasional failures.
            runtime.setProperty("eclipse.p2.mirrors", "false");
            
            IProvisioningAgent agent = runtime.getService(IProvisioningAgent.class);
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
