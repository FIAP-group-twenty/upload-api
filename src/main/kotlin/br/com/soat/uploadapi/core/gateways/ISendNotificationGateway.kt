package br.com.soat.uploadapi.core.gateways

import br.com.soat.uploadapi.infrastructure.gateway.model.NotificationRequest

interface ISendNotificationGateway {
    fun execute(notificationRequest: NotificationRequest)
}