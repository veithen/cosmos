package com.github.veithen.cosmos.osgi.runtime;

import org.osgi.framework.BundleException;

public interface RuntimeInitializer {
    void initializeRuntime(Runtime runtime) throws CosmosException, BundleException;
}
