package br.com.soat.uploadapi.infrastructure.gateway

import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import br.com.soat.uploadapi.infrastructure.persistence.jpa.VideoUploadDataSource
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UploadVideoGateway(
    private val videoUploadDataSource: VideoUploadDataSource,
) : IVideoUploadGateway{

    override fun uploadVideo(upload: VideoUpload) {
        val entity =   VideoUploadEntity(
            idUser = upload.idUser,
            title = upload.title,
            urlVideo = upload.urlVideo,
            urlZipImages = null,
            status = upload.status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        videoUploadDataSource.save(
          entity
        )
    }

}