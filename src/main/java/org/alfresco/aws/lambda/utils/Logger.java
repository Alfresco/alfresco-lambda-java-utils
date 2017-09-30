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
    public static void logInfo(String message, Context context)
    {
        if (context != null)
        {
            context.getLogger().log("INFO " + message);
        }
    }
    
    public static void logWarn(String message, Context context)
    {
        if (context != null)
        {
            context.getLogger().log("WARN " + message);
        }
    }
    
    public static void logDebug(String message, Context context)
    {
        if (context != null)
        {
            context.getLogger().log("DEBUG " + message);
        }
    }
    
    public static void logError(String message, Context context)
    {
        if (context != null)
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
