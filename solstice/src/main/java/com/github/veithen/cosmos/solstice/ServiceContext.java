package com.github.veithen.cosmos.solstice;

import org.osgi.framework.ServiceFactory;

final class ServiceContext {
    private final Service service;
    private final BundleImpl bundle;
    private int refCount;
    private Object serviceObject;
    
    ServiceContext(Service service, BundleImpl bundle) {
        this.service = service;
        this.bundle = bundle;
    }
    
    Object getService() {
        Object serviceObject = this.serviceObject;
        if (serviceObject == null) {
            serviceObject = service.getServiceObject();
            if (serviceObject instanceof ServiceFactory) {
                serviceObject = ((ServiceFactory<Object>)serviceObject).getService(bundle, service);
            }
            this.serviceObject = serviceObject;
        }
        refCount++;
        return serviceObject;
    }
}
