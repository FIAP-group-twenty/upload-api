package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.core.entities.VideoUploadStatus
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import java.io.InputStream


class UploadVideoUseCase(
    private val amazonS3: AmazonS3,
    private val videoUploadGateway: IVideoUploadGateway
) {

    @Value(value = "\${aws.bucketName}")
    private lateinit var bucketName: String

    fun execute(userId: Long, fileName: String, inputStream: InputStream, contentType: String) {

        val metadata = ObjectMetadata().apply {
            this.contentType = contentType
        }

        amazonS3.putObject(bucketName, fileName, inputStream, metadata)
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