package br.com.soat.uploadapi.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Configuration
class AwsConfig(
    @Value("\${aws.key}") private val accessKey: String,
    @Value("\${aws.secret}") private val secretKey: String,
    @Value("\${aws.region}") private val region: String
) {

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .build()
    }

    @Bean
    fun sqsClient(): SqsAsyncClient {
        return SqsAsyncClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(
                StaticCredentialsProvider
                    .create(AwsBasicCredentials.create(accessKey, secretKey))
            )
            .build()
    }
}