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
package com.github.veithen.cosmos.wagon;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class POMHandler extends ArtifactHandler {
    private static final String POM_NS = "http://maven.apache.org/POM/4.0.0";
    
    private final String groupId;
    private final String artifactId;
    private final String version;
    
    public POMHandler(String groupId, String artifactId, String version, IArtifactKey key) {
        super(key);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    private static void addPOMElement(Element parent, String name, String content) {
        Element element = parent.getOwnerDocument().createElementNS(POM_NS, name);
        element.setTextContent(content);
        parent.appendChild(element);
    }
    
    @Override
    protected Resource get(IArtifactRepository artifactRepository, IArtifactDescriptor descriptor, Logger logger) {
        return new Resource() {
            @Override
            public void fetchTo(OutputStream out) throws TransferFailedException, IOException {
                // Generate a POM on the fly
                Document document = DOMUtil.createDocument();
                Element projectElement = document.createElementNS(POM_NS, "project");
                projectElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd");
                document.appendChild(projectElement);
                projectElement.appendChild(document.createComment("Generated dynamically by P2 wagon provider"));
                addPOMElement(projectElement, "modelVersion", "4.0.0");
                addPOMElement(projectElement, "groupId", groupId);
                addPOMElement(projectElement, "artifactId", artifactId);
                addPOMElement(projectElement, "version", version);
                DOMImplementationLS ls = (DOMImplementationLS)document.getImplementation();
                LSSerializer serializer = ls.createLSSerializer();
                LSOutput output = ls.createLSOutput();
                output.setByteStream(out);
                serializer.write(document, output);
            }
        };
    }
}
