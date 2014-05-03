package com.github.veithen.cosmos.osgi.runtime.logging.jdk14;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.veithen.cosmos.osgi.runtime.Runtime;

public final class Jdk14Logger implements com.github.veithen.cosmos.osgi.runtime.logging.Logger {
    public static final Jdk14Logger INSTANCE = new Jdk14Logger(Logger.getLogger(Runtime.class.getName()));
    
    private final Logger logger;

    private Jdk14Logger(Logger logger) {
        this.logger = logger;
    }

    public void debug(String message) {
        logger.log(Level.FINE, message);
    }

    public void debug(String message, Throwable throwable) {
        logger.log(Level.FINE, message, throwable);
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void info(String message, Throwable throwable) {
        logger.log(Level.INFO, message, throwable);
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    public void warn(String message) {
        logger.log(Level.WARNING, message);
    }

    public void warn(String message, Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
    }

    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }
}
