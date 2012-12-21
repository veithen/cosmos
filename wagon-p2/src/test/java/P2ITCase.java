import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.junit.Test;

import com.github.veithen.cosmos.wagon.DefaultRepositoryManager;

public class P2ITCase {
    @Test
    public void test() throws Exception {
        DefaultRepositoryManager repoman = new DefaultRepositoryManager();
        repoman.initialize();
        try {
            IArtifactRepository repository = repoman.loadRepository(new URI(System.getProperty("p2.repo.url")));
            IArtifactKey key = repository.createArtifactKey("osgi.bundle", "org.example.dummy", Version.create("1.0.0"));
            System.out.println(key);
            IArtifactDescriptor[] descriptors = repository.getArtifactDescriptors(key);
            System.out.println(descriptors.length);
            System.out.println(Arrays.asList(descriptors));
            File tmpFile = new File("target/out.jar");
            repository.getArtifact(descriptors[0], new FileOutputStream(tmpFile), new NullProgressMonitor());
            JarInputStream in = new JarInputStream(new FileInputStream(tmpFile));
            try {
                Attributes attrs = in.getManifest().getMainAttributes();
                assertEquals("org.example.dummy", attrs.getValue("Bundle-SymbolicName"));
                assertEquals("1.0.0", attrs.getValue("Bundle-Version"));
            } finally {
                in.close();
            }
        } finally {
            repoman.dispose();
        }
    }
}
