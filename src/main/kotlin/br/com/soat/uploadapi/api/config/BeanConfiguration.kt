package br.com.soat.uploadapi.api.config

import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.core.usecase.UpdateVideoUseCase
import br.com.soat.uploadapi.core.usecase.UploadVideoUseCase
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class BeanConfiguration(
    private val videoGateway: IVideoUploadGateway,
    private val s3Client: S3Client,
    private val sqsTemplate: SqsTemplate
) {

    @Bean
    fun uploadVideoUseCase(): UploadVideoUseCase{
        return UploadVideoUseCase(s3Client, videoGateway, sqsTemplate)
    }

    @Bean
    fun updateVideoUseCase(): UpdateVideoUseCase {
        return UpdateVideoUseCase(videoGateway)
    }
}