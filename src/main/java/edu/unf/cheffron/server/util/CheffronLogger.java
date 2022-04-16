package edu.unf.cheffron.server.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CheffronLogger 
{
    private static final String name = "CheffronWebService";
    private static final Logger logger = Logger.getLogger(name);

    public static void log(Level level, String message)
    {
        logger.log(level, message);
    }

    public static void log(Level level, String message, Throwable throwable)
    {
        logger.log(level, message, throwable);
    }
}
