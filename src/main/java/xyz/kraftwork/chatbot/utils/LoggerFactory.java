/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.chatbot.utils;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author mnunez
 */
public class LoggerFactory {
    public static Logger buildLogger(Class klass){
        Logger logger = Logger.getLogger(klass.getName());
        logger.setLevel(Level.INFO);
        LogManager.getLogManager().reset();
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord lr) {
                System.out.println(String.format("Log level: %s, Class: %s, message: %s", lr.getLevel().toString(), lr.getSourceClassName(), lr.getMessage()));
            }
            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
        return logger;
    }
}
