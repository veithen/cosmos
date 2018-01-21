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
