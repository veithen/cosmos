/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2018 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.cosmos.osgi.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.BundleException;

final class ResourceUtil {
    private ResourceUtil() {}

    static void processResources(String resourceName, ResourceProcessor processor) throws BundleException {
        Enumeration<URL> e;
        try {
            e = Runtime.class.getClassLoader().getResources(resourceName);
        } catch (IOException ex) {
            throw new BundleException(String.format("Failed to load %s resources", resourceName), ex);
        }
        while (e.hasMoreElements()) {
            URL url = e.nextElement();
            try (InputStream in = url.openStream()) {
                processor.process(url, in);
            } catch (IOException ex) {
                throw new BundleException(String.format("Failed to load %s", url), ex);
            }
        }
    }
}
