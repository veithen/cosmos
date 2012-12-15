import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.eclipse.core.runtime.spi.RegistryStrategy;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;
import org.eclipse.ecf.provider.filetransfer.httpclient.HttpClientRetrieveFileTransferFactory;
import org.eclipse.equinox.internal.p2.artifact.repository.ArtifactRepositoryManager;
import org.eclipse.equinox.internal.p2.core.AgentLocation;
import org.eclipse.equinox.internal.p2.core.ProvisioningEventBus;
import org.eclipse.equinox.internal.p2.repository.Transport;
import org.eclipse.equinox.internal.p2.transport.ecf.RepositoryTransport;
import org.eclipse.equinox.internal.provisional.p2.core.eventbus.IProvisioningEventBus;
import org.eclipse.equinox.internal.security.auth.AuthPlugin;
import org.eclipse.equinox.p2.core.IAgentLocation;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;


public class Test {
    public static void main(String[] args) throws Exception {
        RegistryStrategy strat = new RegistryStrategy(new File[] { new File("target/storage") }, new boolean[] { false });
        ExtensionRegistry reg = new ExtensionRegistry(strat, "token", "token");
        Enumeration<URL> e = Test.class.getClassLoader().getResources("plugin.xml");
        RegistryContributor contrib = new RegistryContributor("dummy", "dummy", "dummy", "dummy");
        while (e.hasMoreElements()) {
            URL url = e.nextElement();
            System.out.println("Processing " + url);
            reg.addContribution(url.openStream(), contrib, true, "test", null, "token");
        }
        RegistryFactory.setDefaultRegistryProvider(new MyRegistry(reg));
        
        new AuthPlugin().start(null);
//        new org.eclipse.equinox.internal.p2.repository.Activator() {
//            @Override
//            public IRetrieveFileTransferFactory getRetrieveFileTransferFactory() {
//                return new HttpClientRetrieveFileTransferFactory();
//            }
//        }.start(null);
        
        IProvisioningAgent agent = new MyProvisioningAgent();
        agent.registerService(IAgentLocation.SERVICE_NAME, new AgentLocation(new File("target/p2").getAbsoluteFile().toURI()));
        agent.registerService(IProvisioningEventBus.SERVICE_NAME, new ProvisioningEventBus());
        agent.registerService(Transport.SERVICE_NAME, new RepositoryTransport(agent));
        IArtifactRepositoryManager repoman = new ArtifactRepositoryManager(agent) {
            {
                // Always start with an empty list of repositories and prevent
                // restoreRepositories from accessing the BundleContext (which would fail)
                repositories = new HashMap();
            }
        };
        repoman.loadRepository(new URI("http://download.eclipse.org/releases/juno"), null);
    }
}
