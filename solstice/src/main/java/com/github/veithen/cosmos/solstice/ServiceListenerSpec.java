package com.github.veithen.cosmos.solstice;

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceListener;

final class ServiceListenerSpec {
    private final ServiceListener listener;
    private final Filter filter;
    
    ServiceListenerSpec(ServiceListener listener, Filter filter) {
        this.listener = listener;
        this.filter = filter;
    }

    public ServiceListener getListener() {
        return listener;
    }

    public Filter getFilter() {
        return filter;
    }
}
