package br.com.soat.uploadapi

import br.com.soat.uploadapi.core.exceptions.ConflictException
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.core.usecase.UploadVideoUseCase
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import kotlin.test.assertEquals

class UploadUseCaseTest{

    private lateinit var s3Client: S3Client
    private lateinit var videoUploadGateway: IVideoUploadGateway
    private lateinit var sqsTemplate: SqsTemplate
    private lateinit var uploadVideoUseCase: UploadVideoUseCase

    private val bucketName = "test-bucket"
    private val queue = "test-queue"

    @BeforeEach
    fun setUp() {
        s3Client = mockk(relaxed = true)
        videoUploadGateway = mockk(relaxed = true)
        sqsTemplate = mockk(relaxed = true)

        uploadVideoUseCase = spyk(UploadVideoUseCase(s3Client, videoUploadGateway, sqsTemplate))

        uploadVideoUseCase.apply {
            this::class.java.getDeclaredField("bucketName").apply {
                isAccessible = true
                set(uploadVideoUseCase, bucketName)
            }
            this::class.java.getDeclaredField("queue").apply {
                isAccessible = true
                set(uploadVideoUseCase, queue)
            }
        }
    }

    @Test
    @DisplayName("Deve subir video para processamento")
    fun `should upload video successfully`() {
        val email = "user@example.com"
        val fileName = "video.mp4"
        val file: MultipartFile = mockk {
            every { originalFilename } returns fileName
            every { contentType } returns "video/mp4"
            every { inputStream } returns ByteArrayInputStream(byteArrayOf(1, 2, 3))
        }
        every { videoUploadGateway.findByEmailAndTitle(email, fileName) } returns null
        every { videoUploadGateway.uploadVideo(any()) } just Runs

        assertDoesNotThrow { uploadVideoUseCase.execute(email, file) }

        verify { videoUploadGateway.uploadVideo(match { it.email == email && it.title == fileName }) }
    }


    @Test
    @DisplayName("Deve lançar exception ao tentar subir um video que o usuario ja subiu")
    fun `deve lancar exception ao tentar subir video que ja existe`(){
        val file = MockMultipartFile("file", "video.mp4", "video/mp4", "video content".toByteArray())

        val video = VideoUploadEntity(
                        id =1,
                        email = "lucas@gmail.com",
                        status = "PENDING",
                        createdAt = LocalDateTime.now(),
                        title = "video1",
                        updatedAt = LocalDateTime.now()
                    )
        every { videoUploadGateway.findByEmailAndTitle(any(), any()) } returns video

        val exception = assertThrows<ConflictException> {
            uploadVideoUseCase.execute("lucas@gmail.com", file)
        }

        assertEquals("File already exists", exception.message)
    }


}