package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.core.entities.VideoUploadStatus
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream


class UploadVideoUseCase(
    private val amazonS3: AmazonS3,
    private val videoUploadGateway: IVideoUploadGateway,
) {

    @Value(value = "\${aws.bucketName}")
    private lateinit var bucketName: String

    private val coroutineScope = CoroutineScope(Dispatchers.Default)


    fun execute(userId: Long, file: MultipartFile) {

        val fileName = file.originalFilename ?: "$userId"
        val contentType = file.contentType ?: "application/octet-stream"

        val videoUpload = VideoUpload(
            idUser = userId,
            status = VideoUploadStatus.STARTED.name,
            title = fileName,
            urlVideo = null
        )

        videoUploadGateway.uploadVideo(videoUpload)

        coroutineScope.launch {
            runCatching {
                file.inputStream.use {
                    uploadedVideo(userId, fileName, it, contentType)
                }
            }.onFailure {
                val failedVideo = videoUpload.copy(status = VideoUploadStatus.ERROR.name)
                videoUploadGateway.uploadVideo(failedVideo)

                println("Erro ao processar upload: ${it.message}")
            }
        }

    }

    fun uploadedVideo(userId: Long,
                                      fileName: String,
                                      file: InputStream,
                                      contentType: String) {
        val metadata = ObjectMetadata().apply {
            this.contentType = contentType
        }


        amazonS3.putObject(bucketName, fileName, file, metadata)


        val videoUrl = amazonS3.getUrl(bucketName, fileName).toString()

        val videoUpload = VideoUpload(
            idUser = userId,
            status = VideoUploadStatus.STARTED.name,
            title = fileName,
            urlVideo = videoUrl
        )

        videoUploadGateway.uploadVideo(videoUpload)
    }


}