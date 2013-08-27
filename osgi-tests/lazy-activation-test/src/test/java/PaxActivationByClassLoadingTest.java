import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.MavenUtils.asInProject;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import bundle2.MyService;

@RunWith(JUnit4TestRunner.class)
public class PaxActivationByClassLoadingTest {
    @Inject
    private BundleContext bundleContext;
    
    @Configuration
    public static Option[] configuration() {
        return options(
                mavenBundle().groupId("com.github.veithen.cosmos").artifactId("bundle1").version(asInProject()).start(false),
                mavenBundle().groupId("com.github.veithen.cosmos").artifactId("bundle2").version(asInProject()).start(false),
                junitBundles(),
                frameworkProperty("foo").value("bar"));
    }
    
    @Test
    public void test() throws Exception {
        Bundle bundle1 = OSGiUtil.getBundle(bundleContext, "bundle1");
        Bundle bundle2 = OSGiUtil.getBundle(bundleContext, "bundle2");
        
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
}
