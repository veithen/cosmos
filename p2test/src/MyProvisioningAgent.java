import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.spi.IAgentService;


public class MyProvisioningAgent implements IProvisioningAgent {
    private final Map<String,Object> services = new HashMap<String,Object>();
    
    public void registerService(String serviceName, Object service) {
        services.put(serviceName, service);
        if (service instanceof IAgentService) {
            ((IAgentService)service).start();
        }
    }

    public void unregisterService(String serviceName, Object service) {
        throw new UnsupportedOperationException();
    }

    public Object getService(String serviceName) {
        return services.get(serviceName);
    }

    public void stop() {
    }
}
