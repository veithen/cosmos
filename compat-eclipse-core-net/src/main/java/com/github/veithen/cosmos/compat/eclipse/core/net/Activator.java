package com.github.veithen.cosmos.compat.eclipse.core.net;

import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(IProxyService.class.getName(), new DefaultProxyService(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        
    }
}
