package bundle1;

import bundle1.impl.Activator;

public class Helper {
    // This method will only return the expected result if the bundle
    // has been started. It is used in the tests for lazy activation.
    public static String getProperty(String key) {
        return Activator.getProperty(key);
    }
}
