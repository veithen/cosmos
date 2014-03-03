package com.github.veithen.cosmos.wagon;

import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

public abstract class ArtifactHandler implements ResourceHandler {
    protected final String groupId;
    protected final String artifactId;
    protected final String version;
    
    public ArtifactHandler(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Resource get(IArtifactRepository artifactRepository, Logger logger) {
        Version parsedVersion;
        try {
            parsedVersion = Version.create(version);
        } catch (IllegalArgumentException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(version + " doesn't appear to be a valid bundle version", ex);
            }
            return null;
        }
        IArtifactKey key = artifactRepository.createArtifactKey(groupId, artifactId, parsedVersion);
        IArtifactDescriptor[] descriptors = artifactRepository.getArtifactDescriptors(key);
        if (descriptors.length == 0) {
            return null;
        } else {
            return get(artifactRepository, descriptors[0], logger);
        }
    }
    
    protected abstract Resource get(IArtifactRepository artifactRepository, IArtifactDescriptor descriptor, Logger logger);
}
