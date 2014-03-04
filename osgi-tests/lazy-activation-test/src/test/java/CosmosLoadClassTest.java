import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.github.veithen.cosmos.osgi.runtime.Configuration;
import com.github.veithen.cosmos.osgi.runtime.Runtime;

public class CosmosLoadClassTest {
    @Test
    public void test() throws Exception {
        Runtime runtime = Runtime.getInstance(Configuration.newDefault().build());
        Bundle bundle1 = runtime.getBundle("bundle1");
        assertEquals(Bundle.STARTING, bundle1.getState());
        bundle1.loadClass("bundle1.Helper");
        assertEquals(Bundle.ACTIVE, bundle1.getState());
    }
}
