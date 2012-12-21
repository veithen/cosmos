package com.github.veithen.cosmos.wagon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

/**
 * @plexus.component role="org.apache.maven.wagon.Wagon" role-hint="p2"
 *                   instantiation-strategy="per-lookup"
 */
// TODO: implement StreamingWagon
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
        
        String url = repository.getUrl();
        if (!url.startsWith("p2:")) {
            throw new ConnectionException("Invalid repository URL; expected URL with scheme \"p2\"");
        } else {
            try {
                artifactRepository = repoman.loadRepository(new URI(url.substring(3)));
            } catch (URISyntaxException ex) {
                throw new ConnectionException("Invalid repository URL", ex);
            } catch (ProvisionException ex) {
                throw new ConnectionException("Failed to connect to repository", ex);
            }
        }
    }

    public void get(String resourceName, File destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        getIfNewer(resourceName, destination, 0);
    }

    public boolean getIfNewer(String resourceName, File destination, long timestamp) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        System.out.println(resourceName);
        
        // Translate the resource name back into Maven coordinates and file type
        int fileSlash = resourceName.lastIndexOf('/');
        if (fileSlash == -1) {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
        int versionSlash = resourceName.lastIndexOf('/', fileSlash-1);
        if (fileSlash == -1) {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
        int artifactSlash = resourceName.lastIndexOf('/', versionSlash-1);
        if (artifactSlash == -1) {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
        String groupId = resourceName.substring(0, artifactSlash).replace('/', '.');
        String artifactId = resourceName.substring(artifactSlash+1, versionSlash);
        String version = resourceName.substring(versionSlash+1, fileSlash);
        String file = resourceName.substring(fileSlash+1);
        if (!file.startsWith(artifactId + "-" + version + ".")) {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
        String type = file.substring(artifactId.length() + version.length() + 2);
        System.out.println("groupId=" + groupId);
        System.out.println("artifactId=" + artifactId);
        System.out.println("version=" + version);
        System.out.println("type=" + type);
        
        // Now download the artifact
        if (type.equals("jar")) {
            IArtifactKey key = artifactRepository.createArtifactKey(groupId, artifactId, Version.create(version));
            IArtifactDescriptor[] descriptors = artifactRepository.getArtifactDescriptors(key);
            IArtifactDescriptor descriptor = descriptors[0];
            System.out.println(descriptor.getProperties());
            try {
                FileOutputStream out = new FileOutputStream(destination);
                try {
                    // TODO: process status
                    artifactRepository.getArtifact(descriptor, out, new NullProgressMonitor());
                } finally {
                    out.close();
                }
            } catch (IOException ex) {
                throw new TransferFailedException("Failed to write to " + destination, ex);
            }
        } else {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
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
