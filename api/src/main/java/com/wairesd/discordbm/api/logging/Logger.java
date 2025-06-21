package com.wairesd.discordbm.api.logging;

/**
 * Interface for logging messages
 */
public interface Logger {
    
    /**
     * Log an info message
     * 
     * @param message The message to log
     */
    void info(String message);
    
    /**
     * Log an info message with an exception
     * 
     * @param message The message to log
     * @param throwable The exception to log
     */
    void info(String message, Throwable throwable);
    
    /**
     * Log an info message with arguments
     * 
     * @param message The message to log
     * @param args The arguments to format the message with
     */
    void info(String message, Object... args);
    
    /**
     * Log a warning message
     * 
     * @param message The message to log
     */
    void warn(String message);
    
    /**
     * Log a warning message with an exception
     * 
     * @param message The message to log
     * @param throwable The exception to log
     */
    void warn(String message, Throwable throwable);
    
    /**
     * Log a warning message with arguments
     * 
     * @param message The message to log
     * @param args The arguments to format the message with
     */
    void warn(String message, Object... args);
    
    /**
     * Log an error message
     * 
     * @param message The message to log
     */
    void error(String message);
    
    /**
     * Log an error message with an exception
     * 
     * @param message The message to log
     * @param throwable The exception to log
     */
    void error(String message, Throwable throwable);
    
    /**
     * Log an error message with arguments
     * 
     * @param message The message to log
     * @param args The arguments to format the message with
     */
    void error(String message, Object... args);
} 