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
package com.github.veithen.cosmos.maven;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.publisher.IPublisherAction;
import org.eclipse.equinox.p2.publisher.IPublisherInfo;
import org.eclipse.equinox.p2.publisher.Publisher;
import org.eclipse.equinox.p2.publisher.PublisherInfo;
import org.eclipse.equinox.p2.publisher.eclipse.BundlesAction;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

import com.github.veithen.cosmos.osgi.runtime.Configuration;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.logging.simple.SimpleLogger;
import com.github.veithen.cosmos.p2.P2Initializer;
import com.github.veithen.cosmos.p2.SystemOutProgressMonitor;
import com.github.veithen.mojo.ArtifactProcessingMojo;
import com.github.veithen.mojo.SkippableMojo;

@Mojo(name="create-repository", requiresDependencyResolution=ResolutionScope.TEST)
public class CreateRepositoryMojo extends AbstractMojo implements SkippableMojo, ArtifactProcessingMojo {
    @Parameter(defaultValue="${project.build.directory}/p2-repository", required=true)
    private File outputDirectory;

    @Parameter(defaultValue="${project.build.directory}/p2-agent", required=true)
    private File agentLocation;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            List<Artifact> artifacts = resolveArtifacts();
            URI repoURI = outputDirectory.toURI();
            Runtime runtime = Runtime.getInstance(Configuration.builder().setLogger(SimpleLogger.INSTANCE).setInitializer(new P2Initializer(true)).build());
            IProvisioningAgent agent = runtime.getService(IProvisioningAgentProvider.class).createAgent(agentLocation.toURI());
            IArtifactRepositoryManager artifactRepositoryManager = (IArtifactRepositoryManager)agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
            IMetadataRepositoryManager metadataRepositoryManager = (IMetadataRepositoryManager)agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
            IArtifactRepository artifactRepository = artifactRepositoryManager.createRepository(repoURI, "Artifact Repository", IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY, Collections.<String,String>emptyMap());
            IMetadataRepository metadataRepository = metadataRepositoryManager.createRepository(repoURI, "Metadata Repository", IMetadataRepositoryManager.TYPE_SIMPLE_REPOSITORY, Collections.<String,String>emptyMap());
            PublisherInfo publisherInfo = new PublisherInfo();
            publisherInfo.setArtifactRepository(artifactRepository);
            publisherInfo.setMetadataRepository(metadataRepository);
            publisherInfo.setArtifactOptions(IPublisherInfo.A_PUBLISH | IPublisherInfo.A_INDEX);
            Publisher publisher = new Publisher(publisherInfo);
            List<File> locations = new ArrayList<>();
            for (Artifact artifact : artifacts) {
                locations.add(artifact.getFile());
            }
            publisher.publish(
                    new IPublisherAction[] { new BundlesAction(locations.toArray(new File[locations.size()])) },
                    new SystemOutProgressMonitor());
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }
}
