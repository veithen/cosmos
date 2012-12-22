package bundle2.impl;

import bundle1.Helper;
import bundle2.MyService;

public class MyServiceImpl implements MyService {
    public String getProperty(String key) {
        // Since bundle1 has lazy activation enabled, this call should
        // cause startup of bundle1 (in a real OSGi container).
        return Helper.getProperty(key);
    }
}
