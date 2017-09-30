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

import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.List;

import org.alfresco.aws.lambda.utils.OfflineLambdaContext;
import org.alfresco.aws.lambda.utils.S3Cleaner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Output;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Integration test for the EmptyS3Bucket Lambda function called by CloudFormation.
 *
 * @author Gavin Cornwell
 */
public class EmptyS3BucketIntegrationTest
{
    private static final String CODE_BUCKET_NAME_PREFIX = "empty-s3-bucket-test-code-";
    private static final String STACK_NAME_PREFIX = "empty-s3-bucket-test-";
    private static final String JAR_FILE_NAME = "alfresco-lambda-java-utils-1.0-SNAPSHOT.jar";
    private static final String CFN_FILE_NAME = "cloudformation.yaml";
    private static final String REPLACE_TOKEN = "[code-bucket]";
    
    private static final String OBJECT1_NAME = "object1.txt";
    private static final String OBJECT2_NAME = "object2.txt";
    private static final String OBJECT3_NAME = "object3.txt";
    
    private static AmazonS3 s3;
    private static AmazonCloudFormation cfn;
    private static String codeBucketName;
    private static String contentBucketName;
    private static String cfnTemplate;
    
    @BeforeClass
    public static void setup() throws Exception
    {
        // setup S3 client
        s3 = AmazonS3ClientBuilder.defaultClient();
        cfn = AmazonCloudFormationClientBuilder.defaultClient();
    
        codeBucketName = CODE_BUCKET_NAME_PREFIX + System.currentTimeMillis();
        s3.createBucket(codeBucketName);
        System.out.println("Created code bucket: " + codeBucketName);
        
        // get the maven "target" folder location
        String userDir = System.getProperty("user.dir");
        String targetDir = userDir + File.separator + "target";
        
        // generate cloudformation template from the original
        String cfnInputFilePath = userDir + File.separator + "src" + File.separator + "test" + 
                    File.separator + "resources" + File.separator + CFN_FILE_NAME;
        BufferedReader reader = new BufferedReader(new FileReader(cfnInputFilePath));
        StringWriter writer = new StringWriter();
        
        // read cfn template file into memory, replacing the placeholder with the bucket name
        try
        {
            String line; 
            while (null != ((line = reader.readLine()))) 
            {
                if (line.indexOf(REPLACE_TOKEN) != -1)
                {
                    line = line.replace(REPLACE_TOKEN, codeBucketName);
                }
                
                writer.write(line);
                writer.write("\n");
            }
            
            cfnTemplate = writer.toString();
        }
        finally
        {
            reader.close();
            writer.close();
        }
        
        // upload JAR file to S3 bucket
        File jarFile = new File(targetDir + File.separator + JAR_FILE_NAME);
        System.out.println("Uploading file: " + jarFile.getCanonicalPath() + "...");
        s3.putObject(codeBucketName, JAR_FILE_NAME, jarFile);
        System.out.println("Upload complete");
    }
    
    @AfterClass
    public static void tearDown() throws Exception
    {
        if (s3.doesBucketExistV2(codeBucketName))
        {
            S3Cleaner.emptyBucket(s3, codeBucketName, true, new OfflineLambdaContext());
            System.out.println("Deleted code bucket: " + codeBucketName);
        }
        
        if (s3.doesBucketExistV2(contentBucketName))
        {
            S3Cleaner.emptyBucket(s3, contentBucketName, true, new OfflineLambdaContext());
            System.out.println("Deleted content bucket: " + contentBucketName);
        }
    }
    
    @Test
    public void testTemplateCreateAndDelete() throws Exception
    {
        // create stack
        String stackName = STACK_NAME_PREFIX + System.currentTimeMillis();
        System.out.print("Creating stack: " + stackName + "...");
        CreateStackRequest createRequest = new CreateStackRequest().
                    withStackName(stackName).
                    withTemplateBody(cfnTemplate).
                    withCapabilities("CAPABILITY_IAM").
                    withTimeoutInMinutes(5);
        cfn.createStack(createRequest);
        
        // watch the stack until it completes, checking status every 5 seconds 
        String stackStatus = "CREATE_IN_PROGRESS";
        Stack stack = null;
        while (stackStatus.contains("_IN_PROGRESS"))
        {
            Thread.sleep(5000);
            System.out.print(".");
            
            DescribeStacksRequest describeRequest = new DescribeStacksRequest().withStackName(stackName);
            DescribeStacksResult describeResult = cfn.describeStacks(describeRequest);
            stack = describeResult.getStacks().get(0);
            stackStatus = stack.getStackStatus();
        }
        
        System.out.println("");
        System.out.println("Stack created with status: " + stackStatus);
        
        if ("CREATE_COMPLETE".equals(stackStatus) && stack != null)
        {
            // grab the bucket name from the outputs and store in contentBucketName
            List<Output> outputs = stack.getOutputs();
            for (Output output : outputs)
            {
                if ("BucketName".equals(output.getOutputKey()))
                {
                    contentBucketName = output.getOutputValue();
                    break;
                }
            }
            
            if (contentBucketName != null)
            {
                // add some content to the bucket
                s3.putObject(contentBucketName, OBJECT1_NAME, "This is the content for object 1");
                s3.putObject(contentBucketName, OBJECT2_NAME, "This is the content for object 2");
                s3.putObject(contentBucketName, OBJECT3_NAME, "This is the content for object 3");
            }
        }
        
        // delete the stack
        System.out.print("Deleting stack: " + stackName + "...");
        DeleteStackRequest deleteRequest = new DeleteStackRequest().withStackName(stackName);
        cfn.deleteStack(deleteRequest);
        
        // watch the stack until it fully deletes, checking status every 5 seconds
        stackStatus = "DELETE_IN_PROGRESS";
        while (stackStatus.contains("_IN_PROGRESS"))
        {
            Thread.sleep(5000);
            System.out.print(".");
            
            try
            {
                DescribeStacksRequest describeRequest = new DescribeStacksRequest().withStackName(stackName);
                DescribeStacksResult describeResult = cfn.describeStacks(describeRequest);
                stack = describeResult.getStacks().get(0);
                stackStatus = stack.getStackStatus();
            }
            catch (SdkClientException sce)
            {
                // once the stack has been deleted an exception will
                // be thrown trying to get it's details
                break;
            }
        }
        
        System.out.println("");
        System.out.println("Stack deleted");
        
        // make sure the content bucket has gone
        assertFalse("Bucket '" + contentBucketName + "' should have been deleted", s3.doesBucketExistV2(contentBucketName));
    }
}
