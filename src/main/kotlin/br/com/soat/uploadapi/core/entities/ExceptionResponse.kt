package br.com.soat.uploadapi.core.entities

import java.time.LocalDateTime

data class ExceptionResponse(
    var message: String? = null,
    var uri: String? = null,
    var timestamp: LocalDateTime? = null,
)