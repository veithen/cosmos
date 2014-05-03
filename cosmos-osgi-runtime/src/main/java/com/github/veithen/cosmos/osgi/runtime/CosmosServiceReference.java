package com.github.veithen.cosmos.osgi.runtime;

import org.osgi.framework.ServiceReference;

public interface CosmosServiceReference<T> extends ServiceReference<T> {
    public T getService(BundleImpl bundle);
}
