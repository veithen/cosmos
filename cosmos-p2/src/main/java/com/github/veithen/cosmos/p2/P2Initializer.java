package com.github.veithen.cosmos.p2;

import java.io.File;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.internal.adaptor.BasicLocation;
import org.eclipse.osgi.internal.signedcontent.SignedBundleHook;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.signedcontent.SignedContentFactory;
import org.osgi.framework.BundleException;

import com.github.veithen.cosmos.osgi.runtime.CosmosException;
import com.github.veithen.cosmos.osgi.runtime.Runtime;
import com.github.veithen.cosmos.osgi.runtime.RuntimeInitializer;

public class P2Initializer implements RuntimeInitializer {
    private final File p2DataArea;
    
    public P2Initializer(File p2DataArea) {
        this.p2DataArea = p2DataArea;
    }

    @Override
    public void initializeRuntime(Runtime runtime) throws CosmosException, BundleException {
        runtime.setProperty("eclipse.p2.data.area", p2DataArea.getAbsolutePath());
        // TODO: service registration should be done elsewhere; we should detect that Equinox is used and automatically register these services
        runtime.registerService(null, new String[] { SAXParserFactory.class.getName() }, SAXParserFactory.newInstance(), null);
        runtime.registerService(null, new String[] { Location.class.getName() }, new BasicLocation("dummy", null, false, null), null);
        runtime.registerService(null, new String[] { SignedContentFactory.class.getName() }, new SignedBundleHook(), null);
        runtime.getBundle("org.apache.felix.scr").start();
        runtime.getBundle("org.eclipse.equinox.p2.core").start();
    }
}
