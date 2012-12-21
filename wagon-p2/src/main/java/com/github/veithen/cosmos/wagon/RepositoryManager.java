package com.github.veithen.cosmos.wagon;

import java.net.URI;

import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

public interface RepositoryManager {
    IArtifactRepository loadRepository(URI uri) throws ProvisionException;
}
