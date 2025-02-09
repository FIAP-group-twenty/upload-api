package br.com.soat.uploadapi.infrastructure.gateway.model

data class NotificationRequest(
    val email: String,
    val statusProcess: String,
    val title: String,
)
