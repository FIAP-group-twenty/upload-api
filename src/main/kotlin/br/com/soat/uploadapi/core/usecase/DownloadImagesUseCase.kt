package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.exceptions.NotFoundException
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

class DownloadImagesUseCase(
    private val videoGateway: IVideoUploadGateway,
    @Value(value = "\${aws.bucketName}") private var bucketName: String,
    private val s3Presigner: S3Presigner
) {


    fun execute(email: String, fileName: String): String {
        videoGateway.findByEmailAndTitle(email, fileName)
            ?: throw NotFoundException("File not found: $fileName")

        val request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .getObjectRequest(request)
            .build()

        val presignedUrl = s3Presigner.presignGetObject(presignRequest)
        s3Presigner.close()
        return presignedUrl.url().toString()
    }
}