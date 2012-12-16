package com.github.veithen.cosmos.solstice;

import org.osgi.framework.ServiceReference;

public interface CosmosServiceReference<T> extends ServiceReference<T> {
    public T getService(BundleImpl bundle);
}
