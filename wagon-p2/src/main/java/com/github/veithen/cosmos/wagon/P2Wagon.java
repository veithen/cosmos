package com.github.veithen.cosmos.wagon;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

/**
 * @plexus.component role="org.apache.maven.wagon.Wagon" role-hint="p2"
 *                   instantiation-strategy="per-lookup"
 */
public class P2Wagon extends AbstractWagon {
    /**
     * @plexus.requirement
     */
    private RepositoryManager repoman;
    
    private IArtifactRepository artifactRepository;

    @Override
    protected void openConnectionInternal() throws ConnectionException, AuthenticationException {
//        ProxyInfo proxyInfo = getProxyInfo();
//        ProxyManager.getProxyManager().setProxyData(arg0);
//        System.out.println(ProxyInfoProvider);
        
        try {
            URI p2uri = new URI(repository.getUrl());
            String ssp = p2uri.getSchemeSpecificPart();
            int idx = ssp.indexOf('!');
            if (idx == -1) {
                throw new ConnectionException("Invalid repository URL");
            }
            artifactRepository = repoman.loadRepository(new URI(ssp.substring(0, idx)));
        } catch (URISyntaxException ex) {
            throw new ConnectionException("Invalid repository URL", ex);
        } catch (ProvisionException ex) {
            throw new ConnectionException("Failed to connect to repository", ex);
        }
    }

    public void get(String resourceName, File destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        getIfNewer(resourceName, destination, 0);
    }

    public boolean getIfNewer(String resourceName, File destination, long timestamp) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        System.out.println(resourceName);
        // TODO Auto-generated method stub
        return false;
    }

    public void put(File source, String destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        // TODO Auto-generated method stub
        
    }

    protected void closeConnection() throws ConnectionException {
        // TODO Auto-generated method stub
        
    }
}
