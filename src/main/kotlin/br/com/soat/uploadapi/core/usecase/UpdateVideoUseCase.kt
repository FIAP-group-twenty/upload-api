package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.entities.VideoUpdated
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Value

class UpdateVideoUseCase(
    private val videoUploadGateway: IVideoUploadGateway
) {

    @Value("\${aws.sqs.queue-update}")
    private lateinit var queue: String


    @SqsListener("\${aws.sqs.queue-update}")
    fun startListening(videoUpdated: VideoUpdated) {
        execute(videoUpdated)
    }


    private fun execute(videoUpdated: VideoUpdated) {
        println("Updating video '$videoUpdated'")
        val videoUploadEntity = videoUploadGateway
            .findByEmailAndTitle(videoUpdated.email, videoUpdated.title)
        if (videoUploadEntity == null) throw RuntimeException("Video upload not found")
        videoUploadEntity.status = videoUpdated.status
        videoUploadEntity.urlZipImages = videoUpdated.urlImages
        videoUploadGateway.updatedVideo(videoUploadEntity)
    }



}