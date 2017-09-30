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
package org.alfresco.aws.lambda.model;

import java.util.List;

/**
 * Used to return a response from Lambda to API Gateway when proxy mode is being used.
 */
public class LambdaProxyResponse 
{
    private int statusCode;
    private List<String> headers;
    private String body;

    public int getStatusCode() 
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode) 
    {
        this.statusCode = statusCode;
    }

    public List<String> getHeaders() 
    {
        return headers;
    }

    public void setHeaders(List<String> headers) 
    {
        this.headers = headers;
    }

    public String getBody() 
    {
        return body;
    }

    public void setBody(String body) 
    {
        this.body = body;
    }
}
