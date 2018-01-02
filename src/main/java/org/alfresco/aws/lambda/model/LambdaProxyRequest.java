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
    private String path;
    private String resource;
    private boolean base64Encoded = false;
    private Map<String, String> pathParameters;
    private Map<String, String> queryStringParameters;
    private Map<String, String> headers;
    private Map<String, String> stageVariables;
    private Map<String, Object> requestContext;
     
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
    
    public String getPath() 
    {
        return path;
    }

    public void setPath(String path) 
    {
        this.path = path;
    }
    
    public String getResource() 
    {
        return resource;
    }

    public void setResource(String resource) 
    {
        this.resource = resource;
    }
    
    public boolean isBase64Encoded()
    {
        return this.base64Encoded;
    }

    public void setBase64Encoded(boolean base64Encoded)
    {
        this.base64Encoded = base64Encoded;
    }
    
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

    public Map<String, String> getQueryStringParameters() 
    { 
        return queryStringParameters; 
    }

    public void setQueryStringParameters(Map<String, String> queryStringParameters) 
    { 
        this.queryStringParameters = queryStringParameters; 
    }
    
    public Map<String, String> getStageVariables() 
    { 
        return stageVariables; 
    }

    public void setStageVariables(Map<String, String> stageVariables) 
    { 
        this.stageVariables = stageVariables; 
    }
    
    public Map<String, Object> getRequestContext() 
    { 
        return requestContext; 
    }

    public void setRequestContext(Map<String, Object> requestContext) 
    { 
        this.requestContext = requestContext; 
    }
    
    @Override
    public String toString() 
    {
        StringBuffer buffer = new StringBuffer("{");
        
        buffer.append("httpMethod='");
        buffer.append(this.httpMethod);
        buffer.append("', path='");
        buffer.append(this.path);
        buffer.append("', body='");
        buffer.append(this.body);
        buffer.append("', resource='");
        buffer.append(this.resource);
        buffer.append("', base64Encoded=");
        buffer.append(this.base64Encoded);
        buffer.append(", headers=");
        buffer.append(this.headers);
        buffer.append(", queryStringParameters=");
        buffer.append(this.queryStringParameters);
        buffer.append(", pathParameters=");
        buffer.append(this.pathParameters);
        buffer.append(", requestContext=");
        buffer.append(this.requestContext);
        buffer.append(", stageVariables=");
        buffer.append(this.stageVariables);
        buffer.append("}");
        
        return buffer.toString();
    }
}
