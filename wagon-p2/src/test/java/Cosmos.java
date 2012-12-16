

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Arrays;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.internal.adaptor.BasicLocation;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemBrowserContainerAdapter;
import org.eclipse.ecf.provider.filetransfer.util.ProxySetupHelper;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.osgi.internal.signedcontent.SignedBundleHook;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.signedcontent.SignedContentFactory;

import com.github.veithen.cosmos.solstice.Runtime;

public class Cosmos {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Runtime runtime = Runtime.getInstance();
        runtime.setProperty("eclipse.p2.data.area", new File("target/p2-data").getAbsolutePath());
        runtime.registerService(null, new String[] { SAXParserFactory.class.getName() }, SAXParserFactory.newInstance(), null);
        runtime.registerService(null, new String[] { Location.class.getName() }, new BasicLocation("dummy", null, false, null), null);
        runtime.registerService(null, new String[] { SignedContentFactory.class.getName() }, new SignedBundleHook(), null);
        runtime.getBundle("org.eclipse.equinox.common").start();
        runtime.getBundle("org.eclipse.equinox.registry").start();
        runtime.getBundle("org.eclipse.equinox.security").start();
        runtime.getBundle("org.eclipse.equinox.p2.core").start();
//        runtime.getBundle("org.eclipse.equinox.ds").start();
        runtime.getBundle("org.apache.felix.scr").start();
        runtime.getBundle("org.eclipse.equinox.p2.repository").start();
        runtime.getBundle("org.eclipse.equinox.p2.artifact.repository").start();
        runtime.getBundle("org.eclipse.equinox.p2.updatesite").start();
        runtime.getBundle("org.eclipse.ecf").start();
        runtime.getBundle("org.eclipse.ecf.filetransfer").start();
        runtime.getBundle("org.eclipse.ecf.identity").start();
        runtime.getBundle("org.eclipse.ecf.provider.filetransfer").start();
        runtime.getBundle("org.eclipse.ecf.provider.filetransfer.httpclient").start();
        runtime.getBundle("org.eclipse.equinox.p2.transport.ecf").start();
        
        System.out.println(org.eclipse.equinox.internal.p2.transport.ecf.Activator.getDefault().getRetrieveFileTransferFactory());
        IContainer container = ContainerFactory.getDefault().createContainer();
        IRemoteFileSystemBrowserContainerAdapter adapter = (IRemoteFileSystemBrowserContainerAdapter) container.getAdapter(IRemoteFileSystemBrowserContainerAdapter.class);
        System.out.println(adapter);
        System.out.println(ProxySetupHelper.getProxy("http://www.google.com"));
        new org.apache.commons.httpclient.URI("/test", true, "UTF-8");
        
        IProvisioningAgent agent = runtime.getService(IProvisioningAgent.class);
        IArtifactRepositoryManager repoman = (IArtifactRepositoryManager)agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
//        IProvisioningAgent agent = new ProvisioningAgent();
//        IArtifactRepositoryManager repoman = new ArtifactRepositoryManager(agent);
        IArtifactRepository repository = repoman.loadRepository(new URI("http://download.eclipse.org/releases/juno"), null);
        IArtifactKey key = repository.createArtifactKey("osgi.bundle", "org.eclipse.core.net", Version.create("1.2.200.v20120522-1148"));
        System.out.println(key);
        IArtifactDescriptor[] descriptors = repository.getArtifactDescriptors(key);
        System.out.println(descriptors.length);
        System.out.println(Arrays.asList(descriptors));
        repository.getArtifact(descriptors[0], new FileOutputStream("target/out.jar"), new NullProgressMonitor());
        
        System.exit(0);
    }
}
