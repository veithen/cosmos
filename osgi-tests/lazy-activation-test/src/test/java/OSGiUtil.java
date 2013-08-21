import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public final class OSGiUtil {
    private OSGiUtil() {}

    public static Bundle getBundle(BundleContext bundleContext, String symbolicName) {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().equals(symbolicName)) {
                return bundle;
            }
        }
        throw new IllegalArgumentException("Bundle " + symbolicName + " not found");
    }
}
