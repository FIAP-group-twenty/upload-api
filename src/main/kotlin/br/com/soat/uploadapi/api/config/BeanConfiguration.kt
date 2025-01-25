package br.com.soat.uploadapi.api.config

import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.core.usecase.UploadVideoUseCase
import com.amazonaws.services.s3.AmazonS3
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration(
    private val videoGateway: IVideoUploadGateway,
    private val amazonS3: AmazonS3
) {

    @Bean
    fun uploadVideoUseCase(): UploadVideoUseCase{
        return UploadVideoUseCase(amazonS3, videoGateway)
    }
}