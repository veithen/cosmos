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
