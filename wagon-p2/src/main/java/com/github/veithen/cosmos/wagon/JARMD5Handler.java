package com.github.veithen.cosmos.wagon;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

public class JARMD5Handler extends ArtifactHandler {
    public JARMD5Handler(String classifier, String id, String version) {
        super(classifier, id, version);
    }

    @Override
    protected Resource get(IArtifactRepository artifactRepository, final IArtifactDescriptor descriptor, Logger logger) {
        return new Resource() {
            @Override
            public void fetchTo(OutputStream out) throws TransferFailedException, IOException {
                OutputStreamWriter writer = new OutputStreamWriter(out, "ascii");
                writer.write(descriptor.getProperty(IArtifactDescriptor.DOWNLOAD_MD5));
                writer.flush();
            }
        };
    }
}
