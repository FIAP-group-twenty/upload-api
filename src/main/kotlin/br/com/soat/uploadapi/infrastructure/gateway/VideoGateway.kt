package br.com.soat.uploadapi.infrastructure.gateway

import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.core.gateways.IVideoGateway
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import br.com.soat.uploadapi.infrastructure.persistence.jpa.VideoUploadDataSource
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class VideoGateway(
    private val videoUploadDataSource: VideoUploadDataSource,
) : IVideoGateway{

    override fun uploadVideo(upload: VideoUpload) {
        val entity =   VideoUploadEntity(
            email = upload.email,
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

    override fun findByEmailAndTitle(email: String, title: String): VideoUploadEntity? {
        return videoUploadDataSource.findByEmailAndTitle(email, title)
    }

    override fun updatedVideo(updated: VideoUploadEntity) {
        videoUploadDataSource.save(updated)
    }

    override fun findStatusVideoByUser(email: String): List<VideoUploadEntity?> {
        return videoUploadDataSource.findByEmail(email)
    }
}