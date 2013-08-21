import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.github.veithen.cosmos.osgi.runtime.Configuration;
import com.github.veithen.cosmos.osgi.runtime.Runtime;

public class CosmosGetBundleContextTest {
    @Test
    public void test() throws Exception {
        Runtime runtime = Runtime.getInstance(Configuration.newDefault().build());
        Bundle bundle1 = runtime.getBundle("bundle1");
        assertEquals(Bundle.STARTING, bundle1.getState());
        assertNotNull(bundle1.getBundleContext());
        assertEquals(Bundle.STARTING, bundle1.getState());
    }
}
