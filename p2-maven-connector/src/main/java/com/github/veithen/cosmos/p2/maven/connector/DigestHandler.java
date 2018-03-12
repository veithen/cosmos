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
package com.github.veithen.cosmos.p2.maven.connector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.codehaus.plexus.logging.Logger;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

final class DigestHandler extends ArtifactHandler {
    private final ArtifactHandler parent;
    private final String algorithm;

    public DigestHandler(ArtifactHandler parent, String algorithm) {
        this.parent = parent;
        this.algorithm = algorithm;
    }

    @Override
    void download(Artifact artifact, IArtifactRepository artifactRepository,
            IArtifactDescriptor descriptor, Logger logger, OutputStream out)
            throws IOException, DownloadException {
        DigestOutputStream digester;
        try {
            digester = new DigestOutputStream(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new Error(ex);
        }
        parent.download(artifact, artifactRepository, descriptor, logger, digester);
        OutputStreamWriter writer = new OutputStreamWriter(out, "ascii");
        writer.write(DatatypeConverter.printHexBinary(digester.digest()));
        writer.flush();
    }
}
