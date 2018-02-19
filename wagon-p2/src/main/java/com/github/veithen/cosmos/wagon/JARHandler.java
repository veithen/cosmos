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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;

public class JARHandler extends ArtifactHandler {
    public JARHandler(IArtifactKey key) {
        super(key);
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
