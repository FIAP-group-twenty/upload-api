package br.com.soat.uploadapi.infrastructure.gateway

import br.com.soat.uploadapi.core.exceptions.IntegrationErrorException
import br.com.soat.uploadapi.core.gateways.ISendNotificationGateway
import br.com.soat.uploadapi.infrastructure.gateway.model.NotificationRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class SendNotificationGateway(
    @Autowired private val restTemplate: RestTemplate,
    @Value("\${integrations.baseUrl}") private val baseUrl: String,
) : ISendNotificationGateway {

    override fun execute(notificationRequest: NotificationRequest) {
        try {
            val url = "$baseUrl/api/notifications/v1/send"
            restTemplate.postForObject(url, notificationRequest, String::class.java)
        }catch (e: Exception) {
            throw IntegrationErrorException("Sending notification request failed")
        }
    }

}