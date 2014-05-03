package com.github.veithen.cosmos.osgi.runtime;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import com.github.veithen.cosmos.osgi.runtime.logging.Logger;

final class LogServiceAdapter implements LogService {
    private final Logger logger;

    LogServiceAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(int level, String message) {
        log(level, message, null);
    }

    @Override
    public void log(int level, String message, Throwable exception) {
        switch (level) {
            case LogService.LOG_DEBUG:
                logger.debug(message, exception);
                break;
            case LogService.LOG_INFO:
                logger.info(message, exception);
                break;
            case LogService.LOG_WARNING:
                logger.warn(message, exception);
                break;
            case LogService.LOG_ERROR:
                logger.error(message, exception);
                break;
        }
    }

    @Override
    public void log(ServiceReference sr, int level, String message) {
        log(level, message);
    }

    @Override
    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        log(level, message, exception);
    }
}
