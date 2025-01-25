package br.com.soat.uploadapi.core.entities

data class VideoUpload(
    val idUser: Long,
    val status: String,
    val title: String,
    val urlVideo: String,
)
