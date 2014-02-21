package com.github.veithen.cosmos.wagon;

import org.codehaus.plexus.logging.Logger;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

public interface ResourceHandler {
    Resource get(IArtifactRepository artifactRepository, Logger logger);
}
