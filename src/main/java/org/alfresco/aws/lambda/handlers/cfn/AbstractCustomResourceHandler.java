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
package org.alfresco.aws.lambda.handlers.cfn;

import static org.alfresco.aws.lambda.utils.Logger.logDebug;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.alfresco.aws.lambda.model.CloudFormationRequest;
import org.alfresco.aws.lambda.utils.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractCustomResourceHandler implements RequestHandler<CloudFormationRequest, String> 
{
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    
    public String handleRequest(CloudFormationRequest request, Context context)
    {
        String status = SUCCESS;
        
        logDebug("Recieved CloudFormation request: " + request, context);
        
        // JSON object to hold result data
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.put("Status", status);
        result.put("PhysicalResourceId", getPhysicalResourceId(request));
        result.put("StackId", request.StackId);
        result.put("RequestId", request.RequestId);
        result.put("LogicalResourceId", request.LogicalResourceId);
        ObjectNode data = mapper.createObjectNode();
        result.set("Data", data);
        
        try
        {
            if (CloudFormationRequest.CREATE.equalsIgnoreCase(request.RequestType))
            {
                logDebug("Handling create request...", context);
                handleCreateRequest(request, data, context);
            }
            else if (CloudFormationRequest.UPDATE.equalsIgnoreCase(request.RequestType))
            {
                logDebug("Handling update request...", context);
                handleUpdateRequest(request, data, context);
            }
            else if (CloudFormationRequest.DELETE.equalsIgnoreCase(request.RequestType))
            {
                logDebug("Handling delete request...", context);
                handleDeleteRequest(request, data, context);
            }
        } 
        catch (Exception e)
        {
            status = FAILED;
            result.put("Status", status);
            result.put("Reason", e.getMessage());
        }        
        
        try 
        {
            // send the result to the provided URL
            String body = result.toString();
            sendResult(request.ResponseURL, body, context);
        } 
        catch (Exception e) 
        {
            Logger.logError(e, context);
            status = FAILED;
        }
        
        return status;
    }
    
    public abstract String getPhysicalResourceId(CloudFormationRequest request);
    
    public abstract void handleCreateRequest(CloudFormationRequest request, ObjectNode data, Context context);
    
    public abstract void handleUpdateRequest(CloudFormationRequest request, ObjectNode data, Context context);
    
    public abstract void handleDeleteRequest(CloudFormationRequest request, ObjectNode data, Context context);
    
    
    protected void sendResult(String destinationUrl, String body, Context context) throws IOException 
    {
        logDebug("Uploading result to '" + destinationUrl + "': " + body, context);

        OutputStreamWriter out = null;
        URL url = new URL(destinationUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        try 
        {
            out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.write(body);
        } 
        finally 
        {
            if (out != null) { out.close(); }
        }

        int statusCode = connection.getResponseCode();
        logDebug("Upload completed with status code: " + statusCode, context);
    }
}
