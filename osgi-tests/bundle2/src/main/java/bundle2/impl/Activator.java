package bundle2.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import bundle2.MyService;

public class Activator implements BundleActivator {
    public void start(BundleContext context) throws Exception {
        context.registerService(MyService.class.getName(), new MyServiceImpl(), null);
    }

    public void stop(BundleContext context) throws Exception {
    }
}
