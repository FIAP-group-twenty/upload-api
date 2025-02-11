package br.com.soat.uploadapi.core.entities

data class VideoUpload(
    val email: String,
    val status: String,
    val title: String,
    val urlVideo: String? = null,
)
