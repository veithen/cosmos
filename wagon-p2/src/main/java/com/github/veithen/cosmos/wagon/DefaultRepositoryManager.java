package com.github.veithen.cosmos.wagon;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.core.internal.net.ProxyData;
import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.internal.adaptor.BasicLocation;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.osgi.internal.signedcontent.SignedBundleHook;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.signedcontent.SignedContentFactory;

import com.github.veithen.cosmos.solstice.Runtime;

/**
 * @plexus.component role="com.github.veithen.cosmos.wagon.RepositoryManager"
 */
public class DefaultRepositoryManager implements RepositoryManager, Initializable, Disposable {
    private IArtifactRepositoryManager repoman;
    
    /**
     * @plexus.requirement
     */
    private WagonManager wagonManager;
    
    public void initialize() throws InitializationException {
        try {
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
            runtime.getBundle("org.eclipse.equinox.preferences").start();
            runtime.getBundle("org.eclipse.core.net").start();
            
            // TODO: wagonManager is only null in unit tests; find a way to inject a mock instance
            if (wagonManager != null) {
                System.out.println("Setting up proxy configuration");
                List<IProxyData> proxyDataList = new ArrayList<IProxyData>();
                for (String protocol : new String[] { "http", "https" }) {
                    ProxyInfo proxyInfo = wagonManager.getProxy(protocol);
                    if (proxyInfo != null) {
                        // TODO: we are using an internal class here
                        ProxyData proxyData = new ProxyData(protocol, proxyInfo.getHost(), proxyInfo.getPort(), proxyInfo.getUserName() != null, null);
                        // TODO: add authentication data
                        proxyDataList.add(proxyData);
                    }
                }
                ProxyManager.getProxyManager().setProxyData(proxyDataList.toArray(new IProxyData[proxyDataList.size()]));
            }
            
            IProvisioningAgent agent = runtime.getService(IProvisioningAgent.class);
            repoman = (IArtifactRepositoryManager)agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
        } catch (Exception ex) {
            throw new InitializationException("Failed to initialize P2", ex);
        }
    }

    public IArtifactRepository loadRepository(URI uri) throws ProvisionException {
        return repoman.loadRepository(uri, null);
    }

    public void dispose() {
        
    }
}
