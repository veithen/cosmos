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
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

@Component(role=Wagon.class, hint="p2", instantiationStrategy="per-lookup")
// TODO: implement StreamingWagon
public class P2Wagon extends AbstractWagon implements LogEnabled {
    @Requirement
    private RepositoryManager repoman;
    
    private Logger logger;
    
    private IArtifactRepository artifactRepository;

    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

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

    @Override
    public boolean resourceExists(String resourceName) throws TransferFailedException, AuthorizationException {
        return getResource(resourceName) != null;
    }

    public void get(String resourceName, File destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        getIfNewer(resourceName, destination, 0);
    }

    public boolean getIfNewer(String resourceName, File destination, long timestamp) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Got download request for " + resourceName);
        }
        // TODO: quick & dirty hack: IArtifactRepository is not thread-safe
        synchronized (repoman) {
            Resource resource = getResource(resourceName);
            if (resource == null) {
                throw new ResourceDoesNotExistException(resourceName + " not found");
            } else {
                try {
                    FileOutputStream out = new FileOutputStream(destination);
                    try {
                        resource.fetchTo(out);
                    } finally {
                        out.close();
                    }
                } catch (IOException ex) {
                    throw new TransferFailedException("Failed to write to " + destination, ex);
                }
            }
        }
        // TODO Auto-generated method stub
        return false;
    }
    
    private Resource getResource(String resourceName) {
        ResourceHandler handler = getResourceHandler(resourceName);
        return handler == null ? null : handler.get(artifactRepository, logger);
    }
    
    private ResourceHandler getResourceHandler(String resourceName) {
        if (resourceName.endsWith("/maven-metadata.xml")) {
            int fileSlash = resourceName.lastIndexOf('/');
            int artifactSlash = resourceName.lastIndexOf('/', fileSlash-1);
            if (artifactSlash == -1) {
                return null;
            }
            String groupId = resourceName.substring(0, artifactSlash).replace('/', '.');
            String artifactId = resourceName.substring(artifactSlash+1, fileSlash);
            if (logger.isDebugEnabled()) {
                logger.debug("groupId=" + groupId + "; artifactId=" + artifactId);
            }
            return new MetadataHandler(groupId, artifactId);
        } else {
            // Translate the resource name back into Maven coordinates and file type
            int fileSlash = resourceName.lastIndexOf('/');
            if (fileSlash == -1) {
                return null;
            }
            int versionSlash = resourceName.lastIndexOf('/', fileSlash-1);
            if (versionSlash == -1) {
                return null;
            }
            int artifactSlash = resourceName.lastIndexOf('/', versionSlash-1);
            if (artifactSlash == -1) {
                return null;
            }
            String groupId = resourceName.substring(0, artifactSlash).replace('/', '.');
            String artifactId = resourceName.substring(artifactSlash+1, versionSlash);
            String version = resourceName.substring(versionSlash+1, fileSlash);
            String file = resourceName.substring(fileSlash+1);
            if (!file.startsWith(artifactId + "-" + version)) {
                return null;
            }
            String remainder = file.substring(artifactId.length() + version.length() + 1); // Either ".<type>" or "-<classifier>.<type>"
            if (remainder.length() == 0) {
                return null;
            }
            String classifier;
            String type;
            if (remainder.charAt(0) == '-') {
                int dot = remainder.indexOf('.');
                if (dot == -1) {
                    return null;
                }
                classifier = remainder.substring(1, dot);
                type = remainder.substring(dot+1);
            } else if (remainder.charAt(0) == '.') {
                classifier = null;
                type = remainder.substring(1);
            } else {
                return null;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("groupId=" + groupId + "; artifactId=" + artifactId + "; version=" + version + "; classifier=" + (classifier == null ? "<none>" : classifier) + "; type=" + type);
            }
            if (type.equals("pom")) {
                return new POMHandler(groupId, artifactId, version);
            } else if (type.equals("jar")) {
                if (classifier == null) {
                    return new JARHandler(groupId, artifactId, version);
                } else if (classifier.equals("sources")) {
                    return new JARHandler(groupId, artifactId + ".source", version);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
    
    public void put(File source, String destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        // TODO Auto-generated method stub
        
    }

    protected void closeConnection() throws ConnectionException {
        // TODO Auto-generated method stub
        
    }
}
