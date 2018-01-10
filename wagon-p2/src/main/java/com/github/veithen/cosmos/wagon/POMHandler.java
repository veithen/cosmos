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
    
    public POMHandler(String classifier, String id, String version) {
        super(classifier, id, version);
    }

    private static void addPOMElement(Element parent, String name, String content) {
        Element element = parent.getOwnerDocument().createElementNS(POM_NS, name);
        element.setTextContent(content);
        parent.appendChild(element);
    }
    
    @Override
    protected Resource get(IArtifactRepository artifactRepository, IArtifactDescriptor descriptor, Logger logger) {
        final IArtifactKey artifactKey = descriptor.getArtifactKey();
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
                addPOMElement(projectElement, "groupId", artifactKey.getClassifier());
                addPOMElement(projectElement, "artifactId", artifactKey.getId());
                addPOMElement(projectElement, "version", artifactKey.getVersion().toString());
                DOMImplementationLS ls = (DOMImplementationLS)document.getImplementation();
                LSSerializer serializer = ls.createLSSerializer();
                LSOutput output = ls.createLSOutput();
                output.setByteStream(out);
                serializer.write(document, output);
            }
        };
    }
}
