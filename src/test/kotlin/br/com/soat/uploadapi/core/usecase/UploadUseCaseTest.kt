package br.com.soat.uploadapi.core.usecase


import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.core.entities.VideoUploadStatus
import br.com.soat.uploadapi.core.exceptions.ConflictException
import br.com.soat.uploadapi.core.exceptions.NotFoundException
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import io.awspring.cloud.sqs.operations.SendResult
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Utilities
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
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
        uploadVideoUseCase = UploadVideoUseCase(
            s3Client, videoUploadGateway, sqsTemplate, bucketName, queue
        )
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

    @Test
    fun `should upload videos successfully`() {
        val email = "user@example.com"
        val fileName = "video.mp4"
        val fileContent = "file content".toByteArray()
        val fileStream: InputStream = ByteArrayInputStream(fileContent)
        val contentType = "video/mp4"

        val videoUpload = VideoUploadEntity(
            id = 1,
            email = email,
            status = "PENDING",
            createdAt = LocalDateTime.now(),
            title = fileName,
            updatedAt = LocalDateTime.now()
        )

        val s3Utilities = mockk<S3Utilities>(relaxed = true) // Relaxado para aceitar qualquer chamada
        val putObjectResponse = mockk<PutObjectResponse>()
        val sendResultMock = mockk<SendResult<String>>()
        val mockUrl = URL("https://s3.amazonaws.com/$bucketName/$fileName")

        every { videoUploadGateway.findByEmailAndTitle(email, fileName) } returns videoUpload
        every { s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()) } returns putObjectResponse
        every { s3Client.utilities() } returns s3Utilities
        every { s3Utilities.getUrl(any<GetUrlRequest>()) } returns mockUrl // Corrigido para aceitar qualquer `GetUrlRequest`
        every { videoUploadGateway.updatedVideo(any()) } just Runs
        every { sqsTemplate.send(queue, any<String>()) } returns sendResultMock

        uploadVideoUseCase.uploadedVideo(email, fileName, fileStream, contentType)

        verify { s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()) }
        verify { videoUploadGateway.updatedVideo(any()) }
        verify { sqsTemplate.send(queue, any<String>()) }
    }



    @Test
    fun `should throw NotFoundException when video does not exist`() {
        val email = "user@example.com"
        val fileName = "video.mp4"
        val fileContent = "file content".toByteArray()
        val fileStream: InputStream = ByteArrayInputStream(fileContent)
        val contentType = "video/mp4"

        every { videoUploadGateway.findByEmailAndTitle(email, fileName) } returns null

        val exception = assertThrows<NotFoundException> {
            uploadVideoUseCase.uploadedVideo(email, fileName, fileStream, contentType)
        }

        assertEquals("file does not exists", exception.message)
    }


}