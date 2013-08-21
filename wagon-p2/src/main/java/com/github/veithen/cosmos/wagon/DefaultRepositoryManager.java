package com.github.veithen.cosmos.wagon;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.core.internal.net.ProxyData;
import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;

import com.github.veithen.cosmos.osgi.runtime.Configuration;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.logging.plexus.PlexusLogger;
import com.github.veithen.cosmos.p2.P2Initializer;
import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

/**
 * @plexus.component role="com.github.veithen.cosmos.wagon.RepositoryManager"
 */
public class DefaultRepositoryManager implements RepositoryManager, Initializable, Disposable, LogEnabled {
    private IArtifactRepositoryManager repoman;
    
    /**
     * @plexus.requirement
     */
    private WagonManager wagonManager;
    
    private Logger logger;
    
    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public void initialize() throws InitializationException {
        try {
            // TODO: we need a hack here because the test case instantiates DefaultRepositoryManager directly; instead we should create a Plexus container and look up the component
            Runtime runtime = Runtime.getInstance(Configuration.newDefault().logger(logger == null ? null : new PlexusLogger(logger)).initializer(new P2Initializer(new File("target/p2-data"))).build());
            
            // TODO: wagonManager is only null in unit tests; find a way to inject a mock instance
            if (wagonManager != null) {
                System.out.println("Setting up proxy configuration");
                List<IProxyData> proxyDataList = new ArrayList<IProxyData>();
                for (String protocol : new String[] { "http", "https" }) {
                    ProxyInfo proxyInfo = wagonManager.getProxy(protocol);
                    if (proxyInfo != null) {
                        // TODO: we are using an internal class here
                        ProxyData proxyData = new ProxyData(protocol.toUpperCase(), proxyInfo.getHost(), proxyInfo.getPort(), proxyInfo.getUserName() != null, null);
                        // TODO: add authentication data
                        proxyDataList.add(proxyData);
                    }
                }
                IProxyService proxyManager = ProxyManager.getProxyManager();
                proxyManager.setProxyData(proxyDataList.toArray(new IProxyData[proxyDataList.size()]));
                proxyManager.setSystemProxiesEnabled(false);
            }
            
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
