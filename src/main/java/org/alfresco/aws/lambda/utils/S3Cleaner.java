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

import static org.alfresco.aws.lambda.utils.Logger.logInfo;
import static org.alfresco.aws.lambda.utils.Logger.logWarn;
import static org.alfresco.aws.lambda.utils.Logger.logDebug;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

/**
 * Utility class that provides methods to clean S3 buckets and their contents.
 *
 * @author Gavin Cornwell
 */
public class S3Cleaner
{
    /**
     * Empties the contents of the given bucket and optionally deletes the bucket
     * 
     * @param s3 The S3 client object to use
     * @param bucketName The bucket to empty
     * @param deleteBucket Flag to determine whether to delete the bucket after emptying
     * @param context Lambda context object
     */
    public static void emptyBucket(AmazonS3 s3, String bucketName, boolean deleteBucket, Context context)
    {
        if (s3.doesBucketExistV2(bucketName))
        {
            logDebug("Emptying bucket '" + bucketName + "'...", context);
            
            VersionListing versionListing = s3.listVersions(new ListVersionsRequest().withBucketName(bucketName));
            while (true) 
            {
                for (S3VersionSummary vs : versionListing.getVersionSummaries()) 
                {
                    s3.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
                }
                
                if (versionListing.isTruncated()) 
                {
                    logDebug("Fetching next batch of versions for bucket '" + bucketName + "'", context);
                    versionListing = s3.listNextBatchOfVersions(versionListing);
                } 
                else 
                {
                    break;
                }
            }
            
            logInfo("Successfully emptied bucket '" + bucketName + "'", context);
            
            if (deleteBucket)
            {
                logDebug("Deleting bucket '" + bucketName + "'...", context);
                s3.deleteBucket(bucketName);
                logInfo("Successfully deleted bucket '" + bucketName + "'", context);
            }
        }
        else
        {
            logWarn("Bucket '" + bucketName + "' does not exist", context);
        }
    }
}
