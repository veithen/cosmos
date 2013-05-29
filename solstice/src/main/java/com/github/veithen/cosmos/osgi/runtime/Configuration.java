package com.github.veithen.cosmos.osgi.runtime;

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;
import com.github.veithen.cosmos.osgi.runtime.logging.simple.SimpleLogger;

public final class Configuration {
    public static final class Builder {
        private Logger logger;

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }
        
        public Configuration build() {
            if (logger == null) {
                logger = SimpleLogger.INSTANCE;
            }
            return new Configuration(logger);
        }
    }
    
    private final Logger logger;
    
    Configuration(Logger logger) {
        this.logger = logger;
    }

    public static Builder newDefault() {
        return new Builder();
    }
    
    public Logger getLogger() {
        return logger;
    }
}
