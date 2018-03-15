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
package com.github.veithen.cosmos.p2.maven.plugin.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;

import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;
import com.github.veithen.cosmos.p2.maven.ArtifactCoordinateMapper;
import com.github.veithen.cosmos.p2.maven.P2Coordinate;
import com.github.veithen.cosmos.p2.maven.RepositoryManager;

public abstract class AbstractImportMojo extends AbstractMojo {
    @Component
    private RepositoryManager repositoryManager;

    @Component
    private ArtifactCoordinateMapper artifactCoordinateMapper;

    @Component
    private RepositorySystem repositorySystem;

    @Parameter(property="session.repositorySession", required=true, readonly=true)
    private RepositorySystemSession session;

    @Parameter(defaultValue="${project.artifactId}", required=true)
    private String bundleId;

    @Parameter(property="bundleVersion", required=true)
    private String bundleVersion;

    @Parameter(required=true)
    private String repositoryUri;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        // TODO: use constant for osgi.bundle
        P2Coordinate p2Coordinate = new P2Coordinate("osgi.bundle", transformBundleId(bundleId), Version.parseVersion(bundleVersion));
        Artifact artifact = artifactCoordinateMapper.createArtifact(p2Coordinate);
        try {
            artifact = repositorySystem.resolveArtifact(session, new ArtifactRequest(artifact, null, null)).getArtifact();
        } catch (ArtifactResolutionException ex) {
            // Just continue.
        }
        File outputLocation = getOutputLocation();
        File file = artifact.getFile();
        boolean deleteFile = false;
        try {
            if (file == null) {
                IArtifactRepository repository;
                try {
                    repository = repositoryManager.loadRepository(new URI(repositoryUri));
                } catch (ProvisionException ex) {
                    throw new MojoExecutionException(String.format("Failed to load repository: %s", ex.getMessage()), ex);
                } catch (URISyntaxException ex) {
                    throw new MojoExecutionException(String.format("Invalid repository URI %s", repositoryUri), ex);
                }
                IArtifactDescriptor[] descriptors = repository.getArtifactDescriptors(p2Coordinate.createIArtifactKey(repository));
                if (descriptors.length == 0) {
                    throw new MojoExecutionException(String.format("Bundle %s not found", bundleId));
                }
                if (outputLocation == null) {
                    try {
                        file = File.createTempFile(p2Coordinate.getId(), ".jar");
                    } catch (IOException ex) {
                        throw new MojoExecutionException(String.format("Unable to create temporary file: %s", ex.getMessage()), ex);
                    }
                    deleteFile = true;
                } else {
                    file = outputLocation;
                }
                try (OutputStream out = new FileOutputStream(file)) {
                    // TODO: check status
                    repository.getArtifact(descriptors[0], out, new SystemOutProgressMonitor());
                } catch (IOException ex) {
                    throw new MojoExecutionException(String.format("Unable to download artifact: %s", ex.getMessage()), ex);
                }
                try {
                    repositorySystem.install(session, new InstallRequest().addArtifact(artifact.setFile(file)));
                } catch (InstallationException ex) {
                    throw new MojoExecutionException(String.format("Unable to install artifact: %s", ex.getMessage()), ex);
                }
            } else if (outputLocation != null) {
                try {
                    FileUtils.copyFile(file, outputLocation);
                } catch (IOException ex) {
                    throw new MojoExecutionException("Unable to copy file", ex);
                }
                file = outputLocation;
            }
            processArtifact(file);
        } finally {
            if (deleteFile) {
                file.delete();
            }
        }
    }

    protected String transformBundleId(String bundleId) {
        return bundleId;
    }

    protected abstract File getOutputLocation();
    protected abstract void processArtifact(File file);
}
