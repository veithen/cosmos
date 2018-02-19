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
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.veithen.cosmos.p2.maven.RepositoryManager;

public class P2ITCase {
    private PlexusContainer container;

    @Before
    public void createContainer() throws Exception {
        container = new DefaultPlexusContainer();
    }

    @After
    public void disposeContainer() {
        container.dispose();
    }

    @Test
    public void test() throws Exception {
        // TODO: should no longer be necessary when we get rid of the WagonManager dependency
        container.addComponent(new DefaultRepositorySystem(), RepositorySystem.class, "default");
        RepositoryManager repoman = container.lookup(RepositoryManager.class);
        IArtifactRepository repository = repoman.loadRepository(new URI(System.getProperty("p2.repo.url")));
        IArtifactKey key = repository.createArtifactKey("osgi.bundle", "stax2-api", Version.create("4.0.0"));
        System.out.println(key);
        IArtifactDescriptor[] descriptors = repository.getArtifactDescriptors(key);
        System.out.println(descriptors.length);
        System.out.println(Arrays.asList(descriptors));
        File tmpFile = new File("target/out.jar");
        repository.getArtifact(descriptors[0], new FileOutputStream(tmpFile), new NullProgressMonitor());
        JarInputStream in = new JarInputStream(new FileInputStream(tmpFile));
        try {
            Attributes attrs = in.getManifest().getMainAttributes();
            assertEquals("stax2-api", attrs.getValue("Bundle-SymbolicName"));
            assertEquals("4.0.0", attrs.getValue("Bundle-Version"));
        } finally {
            in.close();
        }
    }
}
