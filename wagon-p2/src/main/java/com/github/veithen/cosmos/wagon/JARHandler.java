package com.github.veithen.cosmos.wagon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

public class JARHandler extends ArtifactHandler {
    public JARHandler(String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
    }

    @Override
    protected Resource get(final IArtifactRepository artifactRepository, final IArtifactDescriptor descriptor, final Logger logger) {
        return new Resource() {
            @Override
            public void fetchTo(File destination) throws TransferFailedException, IOException {
                IStatus status;
                FileOutputStream out = new FileOutputStream(destination);
                try {
                    status = artifactRepository.getArtifact(descriptor, out, new SystemOutProgressMonitor());
                } finally {
                    out.close();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Status: " + status);
                }
                if (!status.isOK()) {
                    throw new TransferFailedException(status.getMessage(), status.getException());
                }
            }
        };
    }
}
