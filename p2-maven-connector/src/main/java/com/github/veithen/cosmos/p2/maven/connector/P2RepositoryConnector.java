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

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.plexus.logging.Logger;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.ArtifactDownload;
import org.eclipse.aether.spi.connector.ArtifactUpload;
import org.eclipse.aether.spi.connector.MetadataDownload;
import org.eclipse.aether.spi.connector.MetadataUpload;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.transfer.ArtifactNotFoundException;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;
import com.github.veithen.cosmos.p2.maven.ArtifactCoordinateMapper;
import com.github.veithen.cosmos.p2.maven.P2Coordinate;

final class P2RepositoryConnector implements RepositoryConnector {
    private static final Map<String,ArtifactHandler> artifactHandlers;
    
    static {
        artifactHandlers = new HashMap<>();
        artifactHandlers.put("jar", new JARHandler());
        artifactHandlers.put("jar.md5", new JARMD5Handler());
        POMHandler pomHandler = new POMHandler();
        artifactHandlers.put("pom", pomHandler);
        artifactHandlers.put("pom.md5", pomHandler);
    }
    
    private final RemoteRepository repository;
    private final IArtifactRepositoryManager artifactRepositoryManager;
    private final ArtifactCoordinateMapper artifactCoordinateMapper;
    private final ProxyHolder proxyHolder;
    private final Logger logger;
    private IArtifactRepository artifactRepository;

    P2RepositoryConnector(RemoteRepository repository, IArtifactRepositoryManager artifactRepositoryManager,
            ArtifactCoordinateMapper artifactCoordinateMapper, ProxyHolder proxyHolder, Logger logger) {
        this.repository = repository;
        this.artifactRepositoryManager = artifactRepositoryManager;
        this.artifactCoordinateMapper = artifactCoordinateMapper;
        this.proxyHolder = proxyHolder;
        this.logger = logger;
    }

    private IArtifactRepository getArtifactRepository() throws DownloadException {
        if (artifactRepository == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Loading repository %s", repository.getUrl()));
            }
            try {
                artifactRepository = artifactRepositoryManager.loadRepository(new URI(repository.getUrl()), new SystemOutProgressMonitor());
            } catch (URISyntaxException | ProvisionException ex) {
                throw new DownloadException(ex);
            }
        }
        return artifactRepository;
    }

    private boolean process(ArtifactDownload artifactDownload) throws DownloadException {
        Artifact artifact = artifactDownload.getArtifact();
        P2Coordinate p2Coordinate = artifactCoordinateMapper.createP2Coordinate(artifact);
        if (p2Coordinate == null) {
            return false;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Resolving artifact %s...", p2Coordinate));
        }
        IArtifactRepository artifactRepository = getArtifactRepository();
        IArtifactDescriptor[] descriptors = artifactRepository.getArtifactDescriptors(p2Coordinate.createIArtifactKey(artifactRepository));
        if (descriptors.length == 0) {
            logger.debug("Not found");
            return false;
        }
        IArtifactDescriptor descriptor = descriptors[0];
        String extension = artifact.getExtension();
        ArtifactHandler artifactHandler = artifactHandlers.get(extension);
        if (artifactHandler == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("No handler found for extension %s", extension));
            }
            return false;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Using handler of type %s", artifactHandler.getClass().getSimpleName()));
        }
        // TODO: write to temporary file
        try (FileOutputStream out = new FileOutputStream(artifactDownload.getFile())) {
            artifactHandler.download(artifact, artifactRepository, descriptor, logger, out);
            logger.debug("Artifact download complete");
            return true;
        } catch (IOException ex) {
            throw new DownloadException(ex);
        }
    }

    private boolean process(MetadataDownload metadataDownload) throws DownloadException {
        IArtifactRepository artifactRepository = getArtifactRepository();
        Metadata metadata = metadataDownload.getMetadata();
        IQueryResult<IArtifactKey> queryResult = artifactRepository.query(
                artifactCoordinateMapper.createArtifactKeyQuery(metadata.getGroupId(), metadata.getArtifactId()),
                new SystemOutProgressMonitor());
        if (queryResult.isEmpty()) {
            return false;
        }
        Document document = DOMUtil.createDocument();
        Element metadataElement = document.createElement("metadata");
        document.appendChild(metadataElement);
        Element groupIdElement = document.createElement("groupId");
        groupIdElement.setTextContent(metadata.getGroupId());
        metadataElement.appendChild(groupIdElement);
        Element artifactIdElement = document.createElement("artifactId");
        artifactIdElement.setTextContent(metadata.getArtifactId());
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
        try (FileOutputStream out = new FileOutputStream(metadataDownload.getFile())) {
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return true;
        } catch (IOException ex) {
            throw new DownloadException(ex);
        } catch (TransformerException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof IOException) {
                throw new DownloadException(cause);
            } else {
                throw new DownloadException(ex);
            }
        }
    }

    @Override
    public void get(Collection<? extends ArtifactDownload> artifactDownloads,
            Collection<? extends MetadataDownload> metadataDownloads) {
        ProxyHolder.Lease lease;
        try {
            lease = proxyHolder.withProxy(repository.getProxy());
        } catch (InterruptedException ex) {
            if (artifactDownloads != null) {
                for (ArtifactDownload artifactDownload : artifactDownloads) {
                    artifactDownload.setException(new ArtifactTransferException(artifactDownload.getArtifact(), repository, ex));
                }
            }
            if (metadataDownloads != null) {
                for (MetadataDownload metadataDownload : metadataDownloads) {
                    metadataDownload.setException(new MetadataTransferException(metadataDownload.getMetadata(), repository, ex));
                }
            }
            Thread.currentThread().interrupt();
            return;
        }
        try {
            if (artifactDownloads != null) {
                for (ArtifactDownload artifactDownload : artifactDownloads) {
                    try {
                        if (!process(artifactDownload)) {
                            artifactDownload.setException(new ArtifactNotFoundException(artifactDownload.getArtifact(), repository));
                        }
                    } catch (DownloadException ex) {
                        logger.debug("Caught exception", ex);
                        artifactDownload.setException(new ArtifactTransferException(artifactDownload.getArtifact(), repository, ex.getMessage(), ex.getCause()));
                    }
                }
            }
            if (metadataDownloads != null) {
                for (MetadataDownload metadataDownload : metadataDownloads) {
                    try {
                        if (!process(metadataDownload)) {
                            metadataDownload.setException(new MetadataNotFoundException(metadataDownload.getMetadata(), repository));
                        }
                    } catch (DownloadException ex) {
                        logger.debug("Caught exception", ex);
                        metadataDownload.setException(new MetadataTransferException(metadataDownload.getMetadata(), repository, ex.getMessage(), ex.getCause()));
                    }
                }
            }
        } finally {
            lease.close();
        }
    }

    @Override
    public void put(Collection<? extends ArtifactUpload> artifactUploads,
            Collection<? extends MetadataUpload> metadataUploads) {
        // TODO: figure out right exception
        if (artifactUploads != null) {
            for (ArtifactUpload artifactDownload : artifactUploads) {
                artifactDownload.setException(new ArtifactNotFoundException(artifactDownload.getArtifact(), repository));
            }
        }
        if (metadataUploads != null) {
            for (MetadataUpload metadataDownload : metadataUploads) {
                metadataDownload.setException(new MetadataNotFoundException(metadataDownload.getMetadata(), repository));
            }
        }
    }

    @Override
    public void close() {

    }
}
