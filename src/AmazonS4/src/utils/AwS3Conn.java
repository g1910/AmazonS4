package utils;

import gui.MainGui;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;

public class AwS3Conn {
	AWSCredentials credentials;
	AmazonS3 s3client;
	String currBucketName;
	ObjectListing objListing;

	public AwS3Conn(String accessKey, String secretAccessKey) {
		credentials = new BasicAWSCredentials(accessKey, secretAccessKey);
		s3client = new AmazonS3Client(credentials);
	}

	public void createBucket(String name) {
		s3client.createBucket(name);
	}

	public AWSCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(AWSCredentials credentials) {
		this.credentials = credentials;
	}

	public AmazonS3 getS3client() {
		return s3client;
	}

	public void setS3client(AmazonS3 s3client) {
		this.s3client = s3client;
	}

	public void setBucket(String name) {
		MainGui.log("Checking if bucket "+name+ " exists...");
		if (s3client.doesBucketExist(name)) {
			MainGui.log("Bucket "+name+ " already exists...");
		} else {
			MainGui.log("Bucket "+name + " does not exist!\nCreating bucket...");
			createBucket(name);
			MainGui.log("Created bucket " + name);
		}
		MainGui.log("Current Bucket set to "+name + "!");
		currBucketName = name;
	}
	
	public String getBucket(){
		return currBucketName;
	}
	
	public ObjectListing getObjectListing(String prefix){
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
        .withBucketName(currBucketName)
        .withPrefix(prefix);
		do {
            objListing = s3client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : 
            	objListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  " +
                        "(size = " + findSize(objectSummary.getSize()) + 
                        " )");
            }
            listObjectsRequest.setMarker(objListing.getNextMarker());
        } while (objListing.isTruncated());
		return objListing;
	}

	public static String findSize(long size) {
		// TODO Auto-generated method stub
		if(size<1024){
			return size + " bytes";
		}else if(size<1024*1024){
			return size*1.0/1024 + "KB";
		}else if(size<1024*1024*1024){
			return size*1.0/(1024*1024) + " MB";
		}else if(size<1024*1024*1024*1024){
			return size*1.0/(1024*1024*1024) + " GB";
		}else return size + "";
	}
}
