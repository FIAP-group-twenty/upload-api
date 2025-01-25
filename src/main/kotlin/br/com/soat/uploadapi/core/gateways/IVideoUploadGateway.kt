package br.com.soat.uploadapi.core.gateways

import br.com.soat.uploadapi.core.entities.VideoUpload

interface IVideoUploadGateway {
    fun uploadVideo(upload: VideoUpload)
}