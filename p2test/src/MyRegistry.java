import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.spi.IRegistryProvider;

public class MyRegistry implements IRegistryProvider {
    private final IExtensionRegistry registry;
    
    public MyRegistry(IExtensionRegistry registry) {
        this.registry = registry;
    }

    public IExtensionRegistry getRegistry() {
        return registry;
    }
}