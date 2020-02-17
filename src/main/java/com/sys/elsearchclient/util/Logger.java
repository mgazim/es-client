package com.sys.elsearchclient.util;

import java.util.logging.Level;

/**
 * Wrapper on {@link java.util.logging.Logger} class
 */
public class Logger {
    private final java.util.logging.Logger log;

    private Logger(String className) {
        this.log = java.util.logging.Logger.getLogger(className);
    }

    public static Logger getLogger(Class clazz) {
        return new Logger(clazz.getSimpleName());
    }

    public void info(String msg) {
        log.log(Level.INFO, msg);
    }

    public void info(String msg, Object... args) {
        log.log(Level.INFO, msg, args);
    }

    public void debug(String msg) {
        log.log(Level.FINE, msg);
    }

    public void debug(String msg, Object... args) {
        log.log(Level.FINE, msg, args);
    }

    public void trace(String msg) {
        log.log(Level.FINEST, msg);
    }

    public void trace(String msg, Object... args) {
        log.log(Level.FINEST, msg, args);
    }

    public void warn(String msg) {
        log.log(Level.WARNING, msg);
    }

    public void warn(String msg, Object... args) {
        log.log(Level.WARNING, msg, args);
    }

    public void error(String msg) {
        log.log(Level.SEVERE, msg);
    }

    public void error(String msg, Object... args) {
        log.log(Level.SEVERE, msg, args);
    }

    // TODO: add arguments
    public void error(String msg, Throwable e) {
        log.log(Level.SEVERE, msg, e);
    }
}
