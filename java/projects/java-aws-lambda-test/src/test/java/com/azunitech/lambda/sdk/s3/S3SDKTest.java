package com.azunitech.lambda.sdk.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkClientException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class S3SDKTest {
    String bucket_name = "testbucket";

    @Test
    public void createBucketTest() {
        AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                "http://127.0.0.1:4566", Regions.US_WEST_2.toString());

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                //.withRegion(Regions.DEFAULT_REGION).build();
                .withEndpointConfiguration(ep).build();
        Bucket b = null;
        if (s3.doesBucketExistV2(bucket_name)) {
            System.out.format("Bucket %s already exists.\n", bucket_name);
            b = getBucket(bucket_name);
        } else {
            try {
                b = s3.createBucket(bucket_name);
                System.out.format("Bucket %s created.\n", bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
    }

    public static Bucket getBucket(String bucket_name) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }

    @Test
    public void delteBucketTest() {
        System.out.println("Deleting S3 bucket: " + bucket_name);
        AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                "http://127.0.0.1:4566", Regions.US_WEST_2.toString());

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                //.withRegion(Regions.DEFAULT_REGION).build();
                .withEndpointConfiguration(ep).build();

        try {
            System.out.println(" - removing objects from bucket");
            ObjectListing object_listing = s3.listObjects(bucket_name);
            while (true) {
                for (Iterator<?> iterator =
                     object_listing.getObjectSummaries().iterator();
                     iterator.hasNext(); ) {
                    S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
                    s3.deleteObject(bucket_name, summary.getKey());
                }

                // more object_listing to retrieve?
                if (object_listing.isTruncated()) {
                    object_listing = s3.listNextBatchOfObjects(object_listing);
                } else {
                    break;
                }
            }

            System.out.println(" - removing versions from bucket");
            VersionListing version_listing = s3.listVersions(
                    new ListVersionsRequest().withBucketName(bucket_name));
            while (true) {
                for (Iterator<?> iterator =
                     version_listing.getVersionSummaries().iterator();
                     iterator.hasNext(); ) {
                    S3VersionSummary vs = (S3VersionSummary) iterator.next();
                    s3.deleteVersion(
                            bucket_name, vs.getKey(), vs.getVersionId());
                }

                if (version_listing.isTruncated()) {
                    version_listing = s3.listNextBatchOfVersions(
                            version_listing);
                } else {
                    break;
                }
            }

            System.out.println(" OK, bucket ready to delete!");
            s3.deleteBucket(bucket_name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }

    @Test
    public void gets3Owner() {
        try {

            AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                    "http://127.0.0.1:4566", Regions.US_WEST_2.toString());

            final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    //.withRegion(Regions.DEFAULT_REGION).build();
                    .withEndpointConfiguration(ep).build();


            final Owner owner1 = s3Client.getS3AccountOwner();
            System.out.println();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    @Test
    public void createBucketWithACLTest() {
        Regions clientRegion = Regions.DEFAULT_REGION;
        String bucketName = "tempbucket1";
        String userEmailForReadPermission = "billgql@gmail.com";

        try {

            AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                    "http://127.0.0.1:4566", Regions.US_WEST_2.toString());

            final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    //.withRegion(Regions.DEFAULT_REGION).build();
                    .withEndpointConfiguration(ep).build();


            // Create a bucket with a canned ACL. This ACL will be replaced by the setBucketAcl()
            // calls below. It is included here for demonstration purposes.
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, clientRegion.getName())
                    .withCannedAcl(CannedAccessControlList.LogDeliveryWrite);
            s3Client.createBucket(createBucketRequest);

            // Create a collection of grants to add to the bucket.
            ArrayList<Grant> grantCollection = new ArrayList<Grant>();

            // Grant the account owner full control.
            Grant grant1 = new Grant(new CanonicalGrantee(s3Client.getS3AccountOwner().getId()), Permission.FullControl);
            grantCollection.add(grant1);

            // Grant the LogDelivery group permission to write to the bucket.
            Grant grant2 = new Grant(GroupGrantee.LogDelivery, Permission.Write);
            grantCollection.add(grant2);

            // Save grants by replacing all current ACL grants with the two we just created.
            AccessControlList bucketAcl = new AccessControlList();
            bucketAcl.grantAllPermissions(grantCollection.toArray(new Grant[0]));
            s3Client.setBucketAcl(bucketName, bucketAcl);

            // Retrieve the bucket's ACL, add another grant, and then save the new ACL.
            AccessControlList newBucketAcl = s3Client.getBucketAcl(bucketName);
            Grant grant3 = new Grant(new EmailAddressGrantee(userEmailForReadPermission), Permission.Read);
            newBucketAcl.grantAllPermissions(grant3);
            s3Client.setBucketAcl(bucketName, newBucketAcl);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }
}
