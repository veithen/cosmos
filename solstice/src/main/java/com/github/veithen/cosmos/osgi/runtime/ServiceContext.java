package com.github.veithen.cosmos.osgi.runtime;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;

final class ServiceContext {
    private final Logger logger;
    private final Service service;
    private final BundleImpl bundle;
    private int refCount;
    private Object serviceObject;
    
    ServiceContext(Logger logger, Service service, BundleImpl bundle) {
        this.logger = logger;
        this.service = service;
        this.bundle = bundle;
    }
    
    Object getService() {
        if (bundle != null && logger.isDebugEnabled()) {
            logger.debug("Bundle " + bundle.getSymbolicName() + " is getting service " + service.getProperty(Constants.SERVICE_ID));
        }
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
