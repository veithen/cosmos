/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2020 Andreas Veithen
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
package com.github.veithen.cosmos.equinox.debug;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true, xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public final class DebugOptionsConfigurator {
    private static final Logger logger = LoggerFactory.getLogger(DebugOptionsConfigurator.class);

    private DebugOptions debugOptions;
    
    @Reference
    private void setDebugOptions(DebugOptions debugOptions) {
        this.debugOptions = debugOptions;
    }
    
    @Activate
    private void activate() {
        if (logger.isDebugEnabled()) {
            debugOptions.setDebugEnabled(true);
            Properties props = new Properties();
            Enumeration<URL> e;
            try {
                e = DebugOptionsConfigurator.class.getClassLoader().getResources("META-INF/cosmos/equinox-debug-options.properties");
            } catch (IOException ex) {
                logger.error("Failed to load META-INF/cosmos/equinox-debug-options.properties", ex);
                return;
            }
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                try (InputStream in = url.openStream()) {
                    props.load(in);
                } catch (IOException ex) {
                    logger.error("Failed to load {}", url, ex);
                }
            }
            props.forEach((key, value) -> debugOptions.setOption((String)key, (String)value));
        }
    }
}
