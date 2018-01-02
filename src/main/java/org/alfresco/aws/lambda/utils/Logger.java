/*
 * #%L
 * Alfresco Lambda Java Utils
 * %%
 * Copyright (C) 2005 - 2017 Alfresco Software Limited
 * %%
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 * #L%
 */
package org.alfresco.aws.lambda.utils;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Utility class that logs messages to the Lambda logger context.
 *
 * @author Gavin Cornwell
 */
public class Logger
{
    public static int LEVEL_OFF = 0;
    public static int LEVEL_ERROR = 1;
    public static int LEVEL_WARN = 2;
    public static int LEVEL_INFO = 3;
    public static int LEVEL_DEBUG = 4;
    
    private static int InternalLogLevel = LEVEL_INFO;
    
    static
    {
        try
        {
            String level = System.getenv("LOG_LEVEL");
            if (level != null && !level.isEmpty())
            {
                if (level.equalsIgnoreCase("ERROR"))
                {
                    InternalLogLevel = LEVEL_ERROR;
                }
                else if (level.equalsIgnoreCase("WARN"))
                {
                    InternalLogLevel = LEVEL_WARN;
                }
                else if (level.equalsIgnoreCase("DEBUG"))
                {
                    InternalLogLevel = LEVEL_DEBUG;
                }
                else
                {
                    InternalLogLevel = LEVEL_INFO;
                }
            }
        }
        catch (Exception e)
        {
            InternalLogLevel = LEVEL_INFO;
        }
    }
    
    public static int getLogLevel()
    {
        return InternalLogLevel;
    }
    
    public static void setLogLevel(int logLevel)
    {
        InternalLogLevel = logLevel;
    }
    
    public static void logInfo(String message, Context context)
    {
        if (context != null && InternalLogLevel >= LEVEL_INFO)
        {
            context.getLogger().log("INFO " + message);
        }
    }
    
    public static void logWarn(String message, Context context)
    {
        if (context != null && InternalLogLevel >= LEVEL_WARN)
        {
            context.getLogger().log("WARN " + message);
        }
    }
    
    public static void logDebug(String message, Context context)
    {
        if (context != null && InternalLogLevel >= LEVEL_DEBUG)
        {
            context.getLogger().log("DEBUG " + message);
        }
    }
    
    public static void logError(String message, Context context)
    {
        if (context != null && InternalLogLevel >= LEVEL_ERROR)
        {
            context.getLogger().log("ERROR " + message);
        }
    }
    
    public static void logError(Throwable error, Context context)
    {
        if (error != null)
        {
            logError(error.getMessage(), context);
        }
    }
}
