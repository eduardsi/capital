package net.sizovs.capital.expenses

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest

class S3Attachment {

    AmazonS3Client s3client

    String key

    S3Attachment(AWSCredentials credentials, String key) {
        this.s3client = new AmazonS3Client(credentials)
        this.key = key
    }

    void write(InputStream inputStream) {
        def request = new PutObjectRequest("capital-expenses", key, inputStream, new ObjectMetadata())
        s3client.putObject(request)
    }

    boolean exists() {
        s3client.doesObjectExist("capital-expenses", key)
    }

    String key() {
        key
    }


}
