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

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;
import com.github.veithen.cosmos.osgi.runtime.logging.simple.SimpleLogger;

public final class Configuration {
    public static final class Builder {
        private Logger logger;
        private RuntimeInitializer initializer;

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }
        
        public Builder initializer(RuntimeInitializer initializer) {
            this.initializer = initializer;
            return this;
        }
        
        public Configuration build() {
            if (logger == null) {
                logger = SimpleLogger.INSTANCE;
            }
            return new Configuration(logger, initializer);
        }
    }
    
    private final Logger logger;
    private final RuntimeInitializer initializer;
    
    Configuration(Logger logger, RuntimeInitializer initializer) {
        this.logger = logger;
        this.initializer = initializer;
    }

    public static Builder newDefault() {
        return new Builder();
    }
    
    public Logger getLogger() {
        return logger;
    }

    public RuntimeInitializer getInitializer() {
        return initializer;
    }
}
