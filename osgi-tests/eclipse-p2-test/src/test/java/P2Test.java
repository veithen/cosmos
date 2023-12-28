/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2022 Andreas Veithen
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
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class P2Test {
    @Inject private Provider<IProvisioningAgentProvider> agentProvider;

    @Test
    public void test() throws Exception {
        IProvisioningAgent agent =
                agentProvider.get().createAgent(new File("target/p2-agent").toURI());
        try {
            IArtifactRepositoryManager artifactRepositoryManager =
                    (IArtifactRepositoryManager)
                            agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
            IMetadataRepositoryManager metadataRepositoryManager =
                    (IMetadataRepositoryManager)
                            agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
            URI repoURI = new File("target/p2-repository").toURI();
            IArtifactRepository artifactRepository =
                    artifactRepositoryManager.createRepository(
                            repoURI,
                            "Artifact Repository",
                            IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY,
                            Collections.<String, String>emptyMap());
            IMetadataRepository metadataRepository =
                    metadataRepositoryManager.createRepository(
                            repoURI,
                            "Metadata Repository",
                            IMetadataRepositoryManager.TYPE_SIMPLE_REPOSITORY,
                            Collections.<String, String>emptyMap());
            PublisherInfo publisherInfo = new PublisherInfo();
            publisherInfo.setArtifactRepository(artifactRepository);
            publisherInfo.setMetadataRepository(metadataRepository);
            publisherInfo.setArtifactOptions(IPublisherInfo.A_PUBLISH | IPublisherInfo.A_INDEX);
            Publisher publisher = new Publisher(publisherInfo);
            IStatus status =
                    publisher.publish(
                            new IPublisherAction[] {
                                new BundlesAction(
                                        new File[] {
                                            new File(
                                                    "target/dependency/org.osgi.framework-1.10.0.jar")
                                        })
                            },
                            new NullProgressMonitor());
            assertThat(status.isOK()).isTrue();
        } finally {
            agent.stop();
        }
        assertThat(
                        new File(
                                        "target/p2-repository/plugins/org.osgi.framework_1.10.0.202007221806.jar")
                                .exists())
                .isTrue();
    }
}
