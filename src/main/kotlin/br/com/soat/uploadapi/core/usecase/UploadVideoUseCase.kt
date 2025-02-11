package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.core.entities.VideoUploadStatus
import br.com.soat.uploadapi.core.exceptions.ConflictException
import br.com.soat.uploadapi.core.exceptions.NotFoundException
import br.com.soat.uploadapi.core.gateways.IVideoGateway
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.time.LocalDateTime


class UploadVideoUseCase(
    private val s3Client: S3Client,
    private val videoUploadGateway: IVideoGateway,
    private val sqsTemplate: SqsTemplate,
    @Value(value = "\${aws.bucketName}") private var bucketName: String,
    @Value("\${aws.sqs.queue-upload}") private var queue: String
) {

    companion object {
        private val objectMapper = ObjectMapper()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)


    fun execute(email: String, file: MultipartFile) {

        val fileName = file.originalFilename ?: email
        val contentType = file.contentType ?: "application/octet-stream"

        val existsFileByUser = videoUploadGateway.findByEmailAndTitle(email, fileName)
        if (existsFileByUser != null) throw ConflictException("File already exists")

        val videoUpload = VideoUpload(
            email = email,
            status = VideoUploadStatus.STARTED.name,
            title = fileName,
            urlVideo = null
        )

        videoUploadGateway.uploadVideo(videoUpload)

        coroutineScope.launch {
            runCatching {
                file.inputStream.use {
                    uploadedVideo(email, fileName, it, contentType)
                }
            }.onFailure {
                val failedVideo = videoUpload.copy(status = VideoUploadStatus.ERROR.name)
                videoUploadGateway.uploadVideo(failedVideo)
            }
        }

    }

    fun uploadedVideo(
        email: String,
        fileName: String,
        file: InputStream,
        contentType: String
    ) {


        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .metadata(mapOf("Content-type" to contentType))
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file, file.available().toLong()))


        val videoUrl = s3Client.utilities().getUrl{ it.bucket(bucketName).key(fileName) }.toString()

        val videoUpload = VideoUpload(
            email = email,
            status = VideoUploadStatus.STARTED.name,
            title = fileName,
            urlVideo = videoUrl
        )


        val findVideo = videoUploadGateway.findByEmailAndTitle(email, fileName)
            ?: throw NotFoundException("file does not exists")
        findVideo.urlVideo = videoUrl
        findVideo.updatedAt = LocalDateTime.now()
        findVideo.status = VideoUploadStatus.IN_PROCESSING.name
        videoUploadGateway.updatedVideo(findVideo)

        val message = objectMapper.writeValueAsString(videoUpload)

        sqsTemplate.send(queue, message)
    }


}