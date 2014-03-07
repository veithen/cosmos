package com.github.veithen.cosmos.wagon;

import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

public abstract class ArtifactHandler implements ResourceHandler {
    private final String classifier;
    private final String id;
    private final String version;
    
    public ArtifactHandler(String classifier, String id, String version) {
        this.classifier = classifier;
        this.id = id;
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
        IArtifactKey key = artifactRepository.createArtifactKey(classifier, id, parsedVersion);
        IArtifactDescriptor[] descriptors = artifactRepository.getArtifactDescriptors(key);
        if (descriptors.length == 0) {
            return null;
        } else {
            return get(artifactRepository, descriptors[0], logger);
        }
    }
    
    protected abstract Resource get(IArtifactRepository artifactRepository, IArtifactDescriptor descriptor, Logger logger);
}
