import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bundle2.MyService;

import com.github.veithen.cosmos.solstice.Runtime;

public class CosmosTest {
    @Test
    public void test() throws Exception {
        Runtime runtime = Runtime.getInstance();
        runtime.setProperty("foo", "bar");
        runtime.getBundle("bundle2").start();
        MyService myService = runtime.getService(MyService.class);
        assertEquals("bar", myService.getProperty("foo"));
    }
}
