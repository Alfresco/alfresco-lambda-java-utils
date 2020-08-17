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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

/**
 * Integration test for the S3Cleaner utility.
 *
 * @author Gavin Cornwell
 */
public class S3CleanerIntegrationTest
{
    private static final String BUCKET_NAME_PREFIX = "s3-cleaner-test-";
    private static final String OBJECT1_NAME = "object1.txt";
    private static final String OBJECT2_NAME = "object2.txt";
    private static final String OBJECT3_NAME = "object3.txt";
    
    private static AmazonS3 s3;
    private static Context offlineContext = new OfflineLambdaContext();
    private String bucketName;
    
    @BeforeClass
    public static void createClient() throws Exception
    {
        // setup S3 client
//        s3 = AmazonS3ClientBuilder.defaultClient();
        s3 = AmazonS3ClientBuilder.standard().build();
    }
    
    @Before
    public void createBucket()
    {
        bucketName = BUCKET_NAME_PREFIX + System.currentTimeMillis();
        s3.createBucket(bucketName);
        System.out.println("TEST Created bucket: " + bucketName);
    }
    
    @After
    public void deleteBucket() throws Exception
    {
        if (s3.doesBucketExistV2(bucketName))
        {
            s3.deleteBucket(bucketName);
            System.out.println("TEST Deleted bucket: " + bucketName);
        }
    }
    
    @Test
    public void testEmptyWithNoVersions() throws Exception
    {
        // add some content to the bucket
        createObjects();
        
        // empty the bucket of it's contents but leave the bucket itself
        S3Cleaner.emptyBucket(s3, bucketName, false, offlineContext);
        
        // check the bucket is still present but empty
        assertTrue("Bucket '" + bucketName + "' should still be present", s3.doesBucketExistV2(bucketName));
        ObjectListing objects = s3.listObjects(bucketName);
        assertTrue("Bucket '" + bucketName + "' should be empty", objects.getObjectSummaries().isEmpty());
    }
    
    @Test
    public void testEmptyWithVersions() throws Exception
    {
        // enable versioning on the bucket
        SetBucketVersioningConfigurationRequest req = new SetBucketVersioningConfigurationRequest(
                    bucketName, new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED));
        s3.setBucketVersioningConfiguration(req);
        
        // create some versions
        createVersionedObjects();
        
        // empty the bucket of it's contents but leave the bucket itself
        S3Cleaner.emptyBucket(s3, bucketName, false, offlineContext);
        
        // check the bucket is still present but empty
        assertTrue("Bucket '" + bucketName + "' should still be present", s3.doesBucketExistV2(bucketName));
        ObjectListing objects = s3.listObjects(bucketName);
        assertTrue("Bucket '" + bucketName + "' should be empty", objects.getObjectSummaries().isEmpty());
    }
    
    @Test
    public void testEmptyAndDeleteBucket() throws Exception
    {
        // add some content to the bucket
        createObjects();
        
        // empty the bucket of it's contents and delete the bucket
        S3Cleaner.emptyBucket(s3, bucketName, true, offlineContext);
        
        // check the bucket has been deleted
        assertFalse("Bucket '" + bucketName + "' should have been deleted", s3.doesBucketExistV2(bucketName));
    }
    
    @Test
    public void testNonExistentBucket() throws Exception
    {
        S3Cleaner.emptyBucket(s3, "bucket-does-not-exist-" + System.currentTimeMillis(), true, offlineContext);
    }
    
    protected void createObjects() throws Exception
    {
        s3.putObject(bucketName, OBJECT1_NAME, "This is the content for object 1");
        s3.putObject(bucketName, OBJECT2_NAME, "This is the content for object 2");
        s3.putObject(bucketName, OBJECT3_NAME, "This is the content for object 3");
    }
    
    protected void createVersionedObjects() throws Exception
    {
        // create the base objects
        createObjects();
        
        // update object 1 three times
        s3.putObject(bucketName, OBJECT1_NAME, "This is the first update");
        s3.putObject(bucketName, OBJECT1_NAME, "This is the second update");
        s3.putObject(bucketName, OBJECT1_NAME, "This is the third update");
    }
}
