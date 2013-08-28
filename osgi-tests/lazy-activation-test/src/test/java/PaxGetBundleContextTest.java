import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.MavenUtils.asInProject;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class PaxGetBundleContextTest {
    @Inject
    private BundleContext bundleContext;
    
    @Configuration
    public static Option[] configuration() {
        return options(
                mavenBundle().groupId("com.github.veithen.cosmos").artifactId("bundle1").version(asInProject()).start(false),
                junitBundles());
    }
    
    @Test
    public void test() throws Exception {
        Bundle bundle1 = OSGiUtil.getBundle(bundleContext, "bundle1");
        assertEquals(Bundle.STARTING, bundle1.getState());
        assertNotNull(bundle1.getBundleContext());
        // Requesting the BundleContext doesn't activate the bundle
        assertEquals(Bundle.STARTING, bundle1.getState());
    }
}
