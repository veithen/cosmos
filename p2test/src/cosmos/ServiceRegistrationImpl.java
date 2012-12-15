package cosmos;

import java.util.Dictionary;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class ServiceRegistrationImpl<T> implements ServiceRegistration<T> {
    public ServiceReference<T> getReference() {
        throw new UnsupportedOperationException();
    }

    public void setProperties(Dictionary<String,?> properties) {
        throw new UnsupportedOperationException();
    }

    public void unregister() {
        throw new UnsupportedOperationException();
    }
}
