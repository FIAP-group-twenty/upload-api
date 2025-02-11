package br.com.soat.uploadapi.core.entities

data class VideoUpdated(
    val email: String,
    val status: String,
    val title: String,
    val urlImages: String? = null,
)
