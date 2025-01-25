package br.com.soat.uploadapi.infrastructure.persistence.jpa

import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VideoUploadDataSource : JpaRepository<VideoUploadEntity, Long> {

}