package br.com.soat.uploadapi.infra.gateways


import br.com.soat.uploadapi.core.exceptions.IntegrationErrorException
import br.com.soat.uploadapi.infrastructure.gateway.SendNotificationGateway
import br.com.soat.uploadapi.infrastructure.gateway.model.NotificationRequest
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate

class SendNotificationGatewayTest {

    private lateinit var restTemplate: RestTemplate
    private lateinit var sendNotificationGateway: SendNotificationGateway
    private val baseUrl = "http://mockserver.com"

    @BeforeEach
    fun setUp() {
        restTemplate = mockk(relaxed = true)
        sendNotificationGateway = SendNotificationGateway(restTemplate, baseUrl)
    }

    @Test
    fun `should send notification successfully`() {
        val notificationRequest = NotificationRequest(
            email = "user@example.com",
            statusProcess = "COMPLETED",
            title = "Test Video"
        )

        every { restTemplate.postForObject("$baseUrl/api/notifications/v1/send", notificationRequest, String::class.java) } returns "Success"

        assertDoesNotThrow { sendNotificationGateway.execute(notificationRequest) }

        verify { restTemplate.postForObject("$baseUrl/api/notifications/v1/send", notificationRequest, String::class.java) }
    }

    @Test
    fun `should throw IntegrationErrorException when request fails`() {
        val notificationRequest = NotificationRequest(
            email = "user@example.com",
            statusProcess = "FAILED",
            title = "Test Video"
        )

        every { restTemplate.postForObject("$baseUrl/api/notifications/v1/send", notificationRequest, String::class.java) } throws RuntimeException("Connection error")

        val exception = assertThrows(IntegrationErrorException::class.java) {
            sendNotificationGateway.execute(notificationRequest)
        }

        assertEquals("Sending notification request failed", exception.message)
        verify { restTemplate.postForObject("$baseUrl/api/notifications/v1/send", notificationRequest, String::class.java) }
    }
}
