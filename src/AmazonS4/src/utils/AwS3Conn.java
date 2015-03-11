package utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class AwS3Conn {
	AWSCredentials credentials;
	AmazonS3 s3client;
	String currBucketName;
	
	public AwS3Conn(String accessKey, String secretAccessKey){
		credentials = new BasicAWSCredentials(accessKey,secretAccessKey);
		s3client = new AmazonS3Client(credentials);
	}
	
	public void createBucket(String name){
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
}
