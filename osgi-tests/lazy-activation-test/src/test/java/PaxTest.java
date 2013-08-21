import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import bundle2.MyService;

@RunWith(JUnit4TestRunner.class)
public class PaxTest {
    @Inject
    private BundleContext bundleContext;
    
    @Configuration
    public static Option[] configuration() {
        return options(
                mavenBundle("com.github.veithen.cosmos", "bundle1").start(false),
                mavenBundle("com.github.veithen.cosmos", "bundle2").start(false),
                junitBundles(),
                frameworkProperty("foo").value("bar"));
    }
    
    @Test
    public void test() throws Exception {
        Bundle bundle1 = getBundle(bundleContext, "bundle1");
        Bundle bundle2 = getBundle(bundleContext, "bundle2");
        
        // Bundles with lazy activation policy are in state STARTING if they have
        // not yet been activated.
        assertEquals(Bundle.STARTING, bundle1.getState());
        assertEquals(Bundle.RESOLVED, bundle2.getState());
        
        bundle2.start();
        MyService myService = (MyService)bundleContext.getService(bundleContext.getServiceReference(MyService.class.getName()));
        assertEquals("bar", myService.getProperty("foo"));
        
        // bundle1 should now have been activated automatically
        assertEquals(Bundle.ACTIVE, bundle1.getState());
    }
    
    private static Bundle getBundle(BundleContext bundleContext, String symbolicName) {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().equals(symbolicName)) {
                return bundle;
            }
        }
        throw new IllegalArgumentException("Bundle " + symbolicName + " not found");
    }
}
