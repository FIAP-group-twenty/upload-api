package br.com.soat.uploadapi.infra.gateways

import br.com.soat.uploadapi.infrastructure.gateway.VideoGateway
import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import br.com.soat.uploadapi.infrastructure.persistence.jpa.VideoUploadDataSource
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.time.LocalDateTime

class VideoGatewayTest {

    private lateinit var videoUploadDataSource: VideoUploadDataSource
    private lateinit var uploadVideoGateway: VideoGateway

    @BeforeEach
    fun setUp() {
        videoUploadDataSource = mockk(relaxed = true)
        uploadVideoGateway = VideoGateway(videoUploadDataSource)
    }

    @Test
    fun `should upload video successfully`() {
        val videoUpload = VideoUpload(
            email = "user@example.com",
            title = "Test Video",
            urlVideo = "https://s3.amazonaws.com/test.mp4",
            status = "PENDING"
        )

        every { videoUploadDataSource.save(any<VideoUploadEntity>()) } returnsArgument 0

        assertDoesNotThrow { uploadVideoGateway.uploadVideo(videoUpload) }

        verify { videoUploadDataSource.save(any<VideoUploadEntity>()) }
    }

    @Test
    fun `should find video by email and title`() {
        val email = "user@example.com"
        val title = "Test Video"
        val videoEntity = VideoUploadEntity(
            id = 1,
            email = email,
            title = title,
            urlVideo = "https://s3.amazonaws.com/test.mp4",
            urlZipImages = null,
            status = "COMPLETED",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { videoUploadDataSource.findByEmailAndTitle(email, title) } returns videoEntity

        val result = uploadVideoGateway.findByEmailAndTitle(email, title)

        assertNotNull(result)
        assertEquals(email, result?.email)
        assertEquals(title, result?.title)

        verify { videoUploadDataSource.findByEmailAndTitle(email, title) }
    }

    @Test
    fun `should update video`() {
        val updatedVideo = VideoUploadEntity(
            id = 1,
            email = "user@example.com",
            title = "Test Video",
            urlVideo = "https://s3.amazonaws.com/test.mp4",
            urlZipImages = "https://s3.amazonaws.com/images.zip",
            status = "PROCESSING",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { videoUploadDataSource.save(any<VideoUploadEntity>()) } returnsArgument 0

        assertDoesNotThrow { uploadVideoGateway.updatedVideo(updatedVideo) }

        verify { videoUploadDataSource.save(updatedVideo) }
    }

    @Test
    fun `should find status video by user`() {
        val email = "user@example.com"
        val videoList = listOf(
            VideoUploadEntity(
                id = 1,
                email = email,
                title = "Test Video",
                urlVideo = "https://s3.amazonaws.com/test.mp4",
                urlZipImages = null,
                status = "COMPLETED",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        every { videoUploadDataSource.findByEmail(email) } returns videoList

        val result = uploadVideoGateway.findStatusVideoByUser(email)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(email, result[0]?.email)

        verify { videoUploadDataSource.findByEmail(email) }
    }
}
