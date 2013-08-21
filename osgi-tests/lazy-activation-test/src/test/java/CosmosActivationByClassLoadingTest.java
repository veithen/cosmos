import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bundle2.MyService;

import com.github.veithen.cosmos.osgi.runtime.Configuration;
import com.github.veithen.cosmos.osgi.runtime.Runtime;

public class CosmosActivationByClassLoadingTest {
    @Test
    public void test() throws Exception {
        Runtime runtime = Runtime.getInstance(Configuration.newDefault().build());
        runtime.setProperty("foo", "bar");
        runtime.getBundle("bundle2").start();
        MyService myService = runtime.getService(MyService.class);
        assertEquals("bar", myService.getProperty("foo"));
    }
}
