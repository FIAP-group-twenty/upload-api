package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.entities.VideoUpdated
import br.com.soat.uploadapi.core.exceptions.NotFoundException
import br.com.soat.uploadapi.core.gateways.ISendNotificationGateway
import br.com.soat.uploadapi.core.gateways.IVideoGateway
import br.com.soat.uploadapi.infrastructure.gateway.model.NotificationRequest
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateVideoUseCaseTest {

    private lateinit var videoUploadGateway: IVideoGateway
    private lateinit var sendNotificationGateway: ISendNotificationGateway
    private lateinit var updateVideoUseCase: UpdateVideoUseCase

    @BeforeEach
    fun setUp() {
        videoUploadGateway = mockk(relaxed = true)
        sendNotificationGateway = mockk(relaxed = true)
        updateVideoUseCase = UpdateVideoUseCase(videoUploadGateway, sendNotificationGateway)
    }

    @Test
    fun `should update video status and send notification when video exists`() {
        val videoUpdated = VideoUpdated(
            email = "user@example.com",
            title = "video.mp4",
            status = "COMPLETED",
            urlImages = "https://example.com/images.zip"
        )

        val videoEntity = mockk<VideoUploadEntity>(relaxed = true)

        every { videoUploadGateway.findByEmailAndTitle(videoUpdated.email, videoUpdated.title) } returns videoEntity

        updateVideoUseCase.startListening(videoUpdated)

        verify { videoEntity.status = videoUpdated.status }
        verify { videoEntity.urlZipImages = videoUpdated.urlImages }
        verify { videoUploadGateway.updatedVideo(videoEntity) }
        verify { sendNotificationGateway.execute(NotificationRequest(videoUpdated.email, videoUpdated.status, videoUpdated.title)) }
    }

    @Test
    fun `should throw NotFoundException when video does not exist`() {
        val videoUpdated = VideoUpdated(
            email = "user@example.com",
            title = "video.mp4",
            status = "COMPLETED",
            urlImages = "https://example.com/images.zip"
        )

        every { videoUploadGateway.findByEmailAndTitle(videoUpdated.email, videoUpdated.title) } returns null

        val exception = assertThrows(NotFoundException::class.java) {
            updateVideoUseCase.startListening(videoUpdated)
        }

        assertEquals("file upload not found", exception.message)
    }
}
