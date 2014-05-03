package com.github.veithen.cosmos.osgi.runtime.logging.simple;

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;

public final class SimpleLogger implements Logger {
    public static final SimpleLogger INSTANCE = new SimpleLogger();
    
    private enum Level { DEBUG, INFO, WARN, ERROR };
    
    private SimpleLogger() {
    }

    private static void log(Level level, String message) {
        System.out.print("[");
        System.out.print(level);
        System.out.print("] ");
        System.out.println(message);
    }
    
    private static void log(Level level, String message, Throwable throwable) {
        log(level, message);
        throwable.printStackTrace(System.out);
    }
    
    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    public void debug(String message, Throwable throwable) {
        log(Level.DEBUG, message, throwable);
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void info(String message, Throwable throwable) {
        log(Level.INFO, message, throwable);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void warn(String message) {
        log(Level.WARN, message);
    }

    public void warn(String message, Throwable throwable) {
        log(Level.WARN, message, throwable);
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }

    public boolean isErrorEnabled() {
        return true;
    }
}
