package com.github.veithen.cosmos.wagon;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

public final class DigestHandler implements ResourceHandler {
    private final ResourceHandler parent;
    private final String algorithm;

    public DigestHandler(ResourceHandler parent, String algorithm) {
        this.parent = parent;
        this.algorithm = algorithm;
    }

    @Override
    public Resource get(IArtifactRepository artifactRepository, Logger logger) {
        final Resource resource = parent.get(artifactRepository, logger);
        return new Resource() {
            @Override
            public void fetchTo(OutputStream out) throws TransferFailedException, IOException {
                DigestOutputStream digester;
                try {
                    digester = new DigestOutputStream(algorithm);
                } catch (NoSuchAlgorithmException ex) {
                    throw new Error(ex);
                }
                resource.fetchTo(digester);
                OutputStreamWriter writer = new OutputStreamWriter(out, "ascii");
                writer.write(DatatypeConverter.printHexBinary(digester.digest()));
                writer.flush();
            }
        };
    }
}
