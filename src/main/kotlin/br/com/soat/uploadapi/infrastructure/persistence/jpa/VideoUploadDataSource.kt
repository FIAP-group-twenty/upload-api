package br.com.soat.uploadapi.infrastructure.persistence.jpa

import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VideoUploadDataSource : JpaRepository<VideoUploadEntity, Long> {
    @Query (
        value = "select * from tb_video_upload where email = :email and title = :title",
        nativeQuery = true)
    fun findByEmailAndTitle(email: String, title: String): VideoUploadEntity?

    fun findByEmail(email: String): List<VideoUploadEntity>
}