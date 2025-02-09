package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.entities.VideoUpdated
import br.com.soat.uploadapi.core.exceptions.NotFoundException
import br.com.soat.uploadapi.core.gateways.ISendNotificationGateway
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.infrastructure.gateway.model.NotificationRequest
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Value


class UpdateVideoUseCase(
    private val videoUploadGateway: IVideoUploadGateway,
    private val sendNotification: ISendNotificationGateway,
) {



    @Value("\${aws.sqs.queue-update}")
    private lateinit var queue: String

    @SqsListener("\${aws.sqs.queue-update}")
    fun startListening(videoUpdated: VideoUpdated) {
        execute(videoUpdated)
    }


    private fun execute(videoUpdated: VideoUpdated) {
        val videoUploadEntity = videoUploadGateway
            .findByEmailAndTitle(videoUpdated.email, videoUpdated.title)
        if (videoUploadEntity == null) throw NotFoundException("file upload not found")
        videoUploadEntity.status = videoUpdated.status
        videoUploadEntity.urlZipImages = videoUpdated.urlImages
        videoUploadGateway.updatedVideo(videoUploadEntity)
        sendNotification.execute(
                    NotificationRequest(
                        videoUpdated.email,
                        videoUpdated.status,
                        videoUpdated.title
                    )
                )
    }
}