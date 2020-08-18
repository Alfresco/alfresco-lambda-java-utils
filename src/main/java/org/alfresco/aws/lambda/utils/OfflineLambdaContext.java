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

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Lambda Context implementation that can be used offline.
 *
 * @author Gavin Cornwell
 */
public class OfflineLambdaContext implements Context
{
    public String getAwsRequestId()
    {
        return null;
    }

    public String getLogGroupName()
    {
        return null;
    }

    public String getLogStreamName()
    {
        return null;
    }

    public String getFunctionName()
    {
        return null;
    }

    public String getFunctionVersion()
    {
        return null;
    }

    public String getInvokedFunctionArn()
    {
        return null;
    }

    public CognitoIdentity getIdentity()
    {
        return null;
    }

    public ClientContext getClientContext()
    {
        return null;
    }

    public int getRemainingTimeInMillis()
    {
        return 0;
    }

    public int getMemoryLimitInMB()
    {
        return 0;
    }

    public LambdaLogger getLogger()
    {
        return new OfflineLambdaLogger();
    }
    
    /**
     * Lambda logger implementation that can be used offline.
     */
    private class OfflineLambdaLogger implements LambdaLogger
    {
        public void log(String string)
        {
            if (string != null)
            {
                System.out.println(string);
            }
        }

        public void log(byte[] bytes)
        {

        }
    }
}
