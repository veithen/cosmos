package com.github.veithen.cosmos.p2;

import java.io.File;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.framework.BundleException;

import com.github.veithen.cosmos.osgi.runtime.CosmosException;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.RuntimeInitializer;
import com.github.veithen.cosmos.osgi.runtime.equinox.EquinoxInitializer;

public class P2Initializer implements RuntimeInitializer {
    private static final String[] optionsForTrace = {
        "org.eclipse.equinox.p2.core/debug",
        "org.eclipse.equinox.p2.core/generator/parsing",
        "org.eclipse.equinox.p2.core/engine/installregistry",
        "org.eclipse.equinox.p2.core/metadata/parsing",
        "org.eclipse.equinox.p2.core/artifacts/mirrors",
        "org.eclipse.equinox.p2.core/core/parseproblems",
        "org.eclipse.equinox.p2.core/planner/operands",
        "org.eclipse.equinox.p2.core/planner/projector",
        "org.eclipse.equinox.p2.core/engine/profilepreferences",
        "org.eclipse.equinox.p2.core/publisher",
        "org.eclipse.equinox.p2.core/reconciler",
        "org.eclipse.equinox.p2.core/core/removeRepo",
        "org.eclipse.equinox.p2.core/updatechecker",
        "org.eclipse.equinox.p2.repository/credentials/debug",
        "org.eclipse.equinox.p2.repository/transport/debug",
        "org.eclipse.ecf/debug",
        "org.eclipse.ecf/debug/exceptions/catching",
        "org.eclipse.ecf/debug/exceptions/throwing",
        "org.eclipse.ecf/debug/methods/entering",
        "org.eclipse.ecf/debug/methods/exiting",
        "org.eclipse.ecf.filetransfer/debug",
        "org.eclipse.ecf.filetransfer/debug/exceptions/throwing",
        "org.eclipse.ecf.filetransfer/debug/exceptions/catching",
        "org.eclipse.ecf.filetransfer/debug/methods/entering",
        "org.eclipse.ecf.filetransfer/debug/methods/exiting",
        "org.eclipse.ecf.provider.filetransfer/debug",
        "org.eclipse.ecf.provider.filetransfer/debug/exceptions/catching",
        "org.eclipse.ecf.provider.filetransfer/debug/exceptions/throwing",
        "org.eclipse.ecf.provider.filetransfer/debug/methods/entering",
        "org.eclipse.ecf.provider.filetransfer/debug/methods/exiting",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/exceptions/catching",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/exceptions/throwing",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/methods/entering",
        "org.eclipse.ecf.provider.filetransfer.httpclient4/debug/methods/exiting",
    };
    
    private final File p2DataArea;
    private final boolean trace;
    
    public P2Initializer(File p2DataArea, boolean trace) {
        this.p2DataArea = p2DataArea;
        this.trace = trace;
    }

    @Override
    public void initializeRuntime(Runtime runtime) throws CosmosException, BundleException {
        // TODO: we should detect that Equinox is used and automatically do this
        EquinoxInitializer.INSTANCE.initializeRuntime(runtime);
        if (trace) {
            DebugOptions debugOptions = runtime.getService(DebugOptions.class);
            for (String option : optionsForTrace) {
                debugOptions.setOption(option, "true");
            }
        }
        runtime.setProperty("eclipse.p2.data.area", p2DataArea.getAbsolutePath());
        runtime.getBundle("org.apache.felix.scr").start();
        runtime.getBundle("org.eclipse.equinox.p2.core").start();
    }
}
