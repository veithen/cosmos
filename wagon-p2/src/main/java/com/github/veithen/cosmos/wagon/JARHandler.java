package com.github.veithen.cosmos.wagon;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

public class JARHandler extends ArtifactHandler {
    public JARHandler(String classifier, String id, String version) {
        super(classifier, id, version);
    }

    @Override
    protected Resource get(final IArtifactRepository artifactRepository, final IArtifactDescriptor descriptor, final Logger logger) {
        return new Resource() {
            @Override
            public void fetchTo(OutputStream out) throws TransferFailedException, IOException {
                IStatus status;
                status = artifactRepository.getArtifact(descriptor, out, new SystemOutProgressMonitor());
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
