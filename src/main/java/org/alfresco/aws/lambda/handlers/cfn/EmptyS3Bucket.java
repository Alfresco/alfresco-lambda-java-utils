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

import org.alfresco.aws.lambda.model.CloudFormationRequest;
import org.alfresco.aws.lambda.utils.S3Cleaner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EmptyS3Bucket extends AbstractCustomResourceHandler
{
    @Override
    public String getPhysicalResourceId(CloudFormationRequest request)
    {
        return "EmptyS3BucketLambda";
    }

    @Override
    public void handleCreateRequest(CloudFormationRequest request, ObjectNode data, Context context)
    {
        logDebug("Nothing to do for create", context);
    }

    @Override
    public void handleUpdateRequest(CloudFormationRequest request, ObjectNode data, Context context)
    {
        logDebug("Nothing to do for update", context);
    }

    @Override
    public void handleDeleteRequest(CloudFormationRequest request, ObjectNode data, Context context)
    {
        // get the bucket name from the request
        String bucketName = (String)request.ResourceProperties.get("BucketName");
        
        logDebug("Retrieved bucket name from request: " + bucketName, context);
        
        // empty and delete the bucket
        S3Cleaner.emptyBucket(AmazonS3ClientBuilder.defaultClient(), bucketName, true, context);
    }
}
