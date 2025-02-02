package br.com.soat.uploadapi.core.gateways

import br.com.soat.uploadapi.core.entities.VideoUpdated
import br.com.soat.uploadapi.core.entities.VideoUpload
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity

interface IVideoUploadGateway {
    fun uploadVideo(upload: VideoUpload)
    fun findByEmailAndTitle(email: String, title: String): VideoUploadEntity?
    fun updatedVideo(updated: VideoUploadEntity)
}