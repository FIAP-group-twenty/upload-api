package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.exceptions.NotFoundException
import br.com.soat.uploadapi.core.gateways.IVideoGateway
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.net.URL

class DownloadVideoUseCaseTest {

    private lateinit var downloadImagesUseCase: DownloadImagesUseCase
    private lateinit var videoUploadGateway: IVideoGateway
    private lateinit var presigner: S3Presigner
    private val bucketName = "test-bucket"


    @BeforeEach
    fun setUp() {
        videoUploadGateway = mockk()
        presigner = mockk()
        downloadImagesUseCase = DownloadImagesUseCase(videoUploadGateway, bucketName, presigner)
        every { presigner.close() } returns Unit
    }

    @Test
    fun `should throw NotFoundException when file does not exist`() {
        val email = "user@example.com"
        val fileName = "video.mp4"
        every { videoUploadGateway.findByEmailAndTitle(email, fileName) } returns null

        val exception = assertThrows<NotFoundException> {
            downloadImagesUseCase.execute(email, fileName)
        }

        assertEquals("File not found: video.mp4", exception.message)
    }

    @Test
    fun `should return presigned URL when file exists`() {
        val email = "user@example.com"
        val fileName = "video.mp4"
        val presignedUrl = URL("https://example.com/presigned-url")

        every { videoUploadGateway.findByEmailAndTitle(email, fileName) } returns mockk()

        val presignRequestSlot = slot<GetObjectPresignRequest>()
        every { presigner.presignGetObject(capture(presignRequestSlot)).url() } returns presignedUrl

        val result = downloadImagesUseCase.execute(email, fileName)

        assertEquals("https://example.com/presigned-url", result)
    }


}