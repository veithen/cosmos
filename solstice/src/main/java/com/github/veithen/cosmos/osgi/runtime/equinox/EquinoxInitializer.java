package com.github.veithen.cosmos.osgi.runtime.equinox;

import org.eclipse.core.runtime.internal.adaptor.BasicLocation;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.internal.signedcontent.SignedBundleHook;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.signedcontent.SignedContentFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.github.veithen.cosmos.osgi.runtime.CosmosException;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.RuntimeInitializer;

public final class EquinoxInitializer implements RuntimeInitializer {
    public static final EquinoxInitializer INSTANCE = new EquinoxInitializer();
    
    private EquinoxInitializer() {}

    @Override
    public void initializeRuntime(Runtime runtime) throws CosmosException, BundleException {
        Bundle bundle = runtime.getBundle("org.eclipse.osgi");
        runtime.registerService(bundle, new String[] { Location.class.getName() }, new BasicLocation("dummy", null, false, null), null);
        runtime.registerService(bundle, new String[] { SignedContentFactory.class.getName() }, new SignedBundleHook(), null);
        runtime.registerService(bundle, new String[] { DebugOptions.class.getName() }, new DebugOptionsImpl(), null);
        runtime.registerService(bundle, new String[] { FrameworkLog.class.getName() }, new FrameworkLogAdapter(runtime.getLogger()), null);
    }
}
