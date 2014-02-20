package com.github.veithen.cosmos.wagon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.repository.artifact.ArtifactKeyQuery;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

/**
 * @plexus.component role="org.apache.maven.wagon.Wagon" role-hint="p2"
 *                   instantiation-strategy="per-lookup"
 */
// TODO: implement StreamingWagon
public class P2Wagon extends AbstractWagon implements LogEnabled {
    /**
     * @plexus.requirement
     */
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

    public void get(String resourceName, File destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        getIfNewer(resourceName, destination, 0);
    }

    public boolean getIfNewer(String resourceName, File destination, long timestamp) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Got download request for " + resourceName);
        }
        try {
            if (resourceName.endsWith("/maven-metadata.xml")) {
                processMetadataRequest(resourceName, destination);
            } else {
                processArtifactRequest(resourceName, destination);
            }
        } catch (IOException ex) {
            throw new TransferFailedException("Failed to write to " + destination, ex);
        }
        // TODO Auto-generated method stub
        return false;
    }
    
    private void processMetadataRequest(String resourceName, File destination) throws ResourceDoesNotExistException, IOException {
        // Note: if we get here, then fileSlash can't be -1
        int fileSlash = resourceName.lastIndexOf('/');
        int artifactSlash = resourceName.lastIndexOf('/', fileSlash-1);
        if (artifactSlash == -1) {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
        String groupId = resourceName.substring(0, artifactSlash).replace('/', '.');
        String artifactId = resourceName.substring(artifactSlash+1, fileSlash);
        if (logger.isDebugEnabled()) {
            logger.debug("groupId=" + groupId + "; artifactId=" + artifactId);
        }
        IQueryResult<IArtifactKey> queryResult = artifactRepository.query(new ArtifactKeyQuery(groupId, artifactId, null), new SystemOutProgressMonitor());
        if (queryResult.isEmpty()) {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new Error(ex);
        }
        Element metadataElement = document.createElement("metadata");
        document.appendChild(metadataElement);
        Element groupIdElement = document.createElement("groupId");
        groupIdElement.setTextContent(groupId);
        metadataElement.appendChild(groupIdElement);
        Element artifactIdElement = document.createElement("artifactId");
        artifactIdElement.setTextContent(artifactId);
        metadataElement.appendChild(artifactIdElement);
        Element versioningElement = document.createElement("versioning");
        metadataElement.appendChild(versioningElement);
        Element versionsElement = document.createElement("versions");
        versioningElement.appendChild(versionsElement);
        for (IArtifactKey artifactKey : queryResult) {
            Element versionElement = document.createElement("version");
            versionElement.setTextContent(artifactKey.getVersion().toString());
            versionsElement.appendChild(versionElement);
        }
        // TODO: need to fill in metadata/versioning/lastUpdated ??
        // TODO: use DOM LS API here
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new Error(ex);
        }
        try {
            transformer.transform(new DOMSource(document), new StreamResult(destination));
        } catch (TransformerException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            } else {
                throw new IOException(ex);
            }
        }
    }
    
    private void processArtifactRequest(String resourceName, File destination) throws ResourceDoesNotExistException, IOException {
        // Translate the resource name back into Maven coordinates and file type
        int fileSlash = resourceName.lastIndexOf('/');
        if (fileSlash == -1) {
            throw new ResourceDoesNotExistException(resourceName + " not found");
        }
        int versionSlash = resourceName.lastIndexOf('/', fileSlash-1);
        if (versionSlash == -1) {
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
        if (logger.isDebugEnabled()) {
            logger.debug("groupId=" + groupId + "; artifactId=" + artifactId + "; version=" + version + "; type=" + type);
        }
        
        // Now download the artifact
        if (type.equals("jar")) {
            IArtifactKey key = artifactRepository.createArtifactKey(groupId, artifactId, Version.create(version));
            IArtifactDescriptor[] descriptors = artifactRepository.getArtifactDescriptors(key);
            if (descriptors.length == 0) {
                throw new ResourceDoesNotExistException(resourceName + " not found: no matching artifact found");
            }
            IArtifactDescriptor descriptor = descriptors[0];
            System.out.println(descriptor.getProperties());
            FileOutputStream out = new FileOutputStream(destination);
            try {
                // TODO: process status
                artifactRepository.getArtifact(descriptor, out, new SystemOutProgressMonitor());
            } finally {
                out.close();
            }
        } else {
            throw new ResourceDoesNotExistException(resourceName + " not found: artifact type " + type + " not supported");
        }
    }

    public void put(File source, String destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        // TODO Auto-generated method stub
        
    }

    protected void closeConnection() throws ConnectionException {
        // TODO Auto-generated method stub
        
    }
}
