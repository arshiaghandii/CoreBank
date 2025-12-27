package ir.tejaratBank.core.CoreBank.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalLogger {

    // Private constructor to prevent instantiation
    private GlobalLogger() {}

    public static void info(Class<?> sourceClass, String message) {
        LogManager.getLogger(sourceClass).info(message);
    }

    public static void debug(Class<?> sourceClass, String message) {
        LogManager.getLogger(sourceClass).debug(message);
    }

    public static void error(Class<?> sourceClass, String message, Throwable throwable) {
        LogManager.getLogger(sourceClass).error(message, throwable);
    }

    public static void warn(Class<?> sourceClass, String message) {
        LogManager.getLogger(sourceClass).warn(message);
    }
}