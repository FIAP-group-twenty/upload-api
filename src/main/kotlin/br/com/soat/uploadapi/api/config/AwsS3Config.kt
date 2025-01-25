package br.com.soat.uploadapi.api.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.S3ClientOptions
import org.flywaydb.core.api.configuration.S3ClientFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config(
    @Value("\${aws.key}") private val accessKey: String,
    @Value("\${aws.secret}") private val secretKey: String,
    @Value("\${aws.region}") private val region: String
) {

    @Bean
    fun s3Client(): AmazonS3{
        val credentials = BasicAWSCredentials(accessKey, secretKey)

        return AmazonS3ClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(
                AWSStaticCredentialsProvider(
                    credentials
                )
            )
            .build()
    }
}