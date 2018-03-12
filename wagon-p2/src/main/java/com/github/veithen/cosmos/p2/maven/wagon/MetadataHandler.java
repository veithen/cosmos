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
package com.github.veithen.cosmos.p2.maven.wagon;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.repository.artifact.ArtifactKeyQuery;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

public class MetadataHandler implements ResourceHandler {
    private final String groupId;
    private final String artifactId;
    private final ArtifactKeyQuery artifactKeyQuery;

    public MetadataHandler(String groupId, String artifactId, ArtifactKeyQuery artifactKeyQuery) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.artifactKeyQuery = artifactKeyQuery;
    }

    public Resource get(IArtifactRepository artifactRepository, Logger logger) {
        final IQueryResult<IArtifactKey> queryResult = artifactRepository.query(artifactKeyQuery, new SystemOutProgressMonitor());
        if (queryResult.isEmpty()) {
            return null;
        } else {
            return new Resource() {
                @Override
                public void fetchTo(OutputStream out) throws TransferFailedException, IOException {
                    Document document = DOMUtil.createDocument();
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
                        transformer.transform(new DOMSource(document), new StreamResult(out));
                    } catch (TransformerException ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof IOException) {
                            throw (IOException)cause;
                        } else {
                            throw new IOException(ex);
                        }
                    }
                }
            };
        }
    }
}
