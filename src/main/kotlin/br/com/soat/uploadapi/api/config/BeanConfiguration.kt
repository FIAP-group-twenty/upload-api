package br.com.soat.uploadapi.api.config

import br.com.soat.uploadapi.core.gateways.ISendNotificationGateway
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.core.usecase.DownloadImagesUseCase
import br.com.soat.uploadapi.core.usecase.FindStatusVideoByUserUseCase
import br.com.soat.uploadapi.core.usecase.UpdateVideoUseCase
import br.com.soat.uploadapi.core.usecase.UploadVideoUseCase
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class BeanConfiguration(
    private val videoGateway: IVideoUploadGateway,
    private val s3Client: S3Client,
    private val sqsTemplate: SqsTemplate,
    private val sendNotificationGateway: ISendNotificationGateway,
    @Value(value = "\${aws.bucketName}") private var bucketName: String,
    private val presigner: S3Presigner,
    @Value("\${aws.sqs.queue-upload}") private var queue: String

) {

    @Bean
    fun uploadVideoUseCase(): UploadVideoUseCase{
        return UploadVideoUseCase(s3Client, videoGateway, sqsTemplate, bucketName, queue)
    }

    @Bean
    fun updateVideoUseCase(): UpdateVideoUseCase {
        return UpdateVideoUseCase(videoGateway, sendNotificationGateway)
    }

    @Bean
    fun findStatusVideoByUserUseCase(): FindStatusVideoByUserUseCase {
        return FindStatusVideoByUserUseCase(videoGateway)
    }

    @Bean
    fun downloadImagesUseCase(): DownloadImagesUseCase {
        return DownloadImagesUseCase(videoGateway, bucketName, presigner)
    }
}