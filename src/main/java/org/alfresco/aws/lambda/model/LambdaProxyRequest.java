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

import java.util.Map;

/**
 * Used to pass a request to Lambda from API Gateway when proxy mode is being used.
 */
public class LambdaProxyRequest 
{
    private String httpMethod;
    private String body;
    private Map<String, String> pathParameters;
    private Map<String, String> queryParameters;
    private Map<String, String> headers;

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }

    public Map<String, String> getPathParameters()
    {
        return pathParameters;
    }

    public void setPathParameters(Map<String, String> pathParameters) 
    {
        this.pathParameters = pathParameters;
    }

    public Map<String, String> getQueryParameters() 
    { 
        return queryParameters; 
    }

    public void setQueryParameters(Map<String, String> queryParameters) 
    { 
        this.queryParameters = queryParameters; 
    }

    public String getHttpMethod() 
    {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) 
    {
        this.httpMethod = httpMethod;
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
