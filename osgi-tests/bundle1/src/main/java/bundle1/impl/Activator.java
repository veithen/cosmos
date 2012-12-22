package bundle1.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    private static BundleContext bundleContext;

    public void start(BundleContext context) throws Exception {
        bundleContext = context;
    }

    public void stop(BundleContext context) throws Exception {
        bundleContext = null;
    }
    
    public static String getProperty(String key) {
        return bundleContext == null ? null : bundleContext.getProperty(key);
    }
}
