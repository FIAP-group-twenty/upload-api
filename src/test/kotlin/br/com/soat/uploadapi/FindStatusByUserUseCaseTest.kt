package br.com.soat.uploadapi

import br.com.soat.uploadapi.core.gateways.IVideoUploadGateway
import br.com.soat.uploadapi.core.usecase.FindStatusVideoByUserUseCase
import br.com.soat.uploadapi.infrastructure.persistence.enitites.VideoUploadEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FindStatusByUserUseCaseTest {

    private lateinit var findStatusVideoByUserUseCase: FindStatusVideoByUserUseCase
    private lateinit var videoUploadGateway: IVideoUploadGateway

    @BeforeEach
    fun setup(){
        videoUploadGateway = mockk(relaxed = true)
        findStatusVideoByUserUseCase = FindStatusVideoByUserUseCase(videoUploadGateway)
    }

    @Test
    @DisplayName("Deve retornar lista de arquivos do usuario")
    fun `should return list of video statuses when videos exist`() {
        val email = "user@example.com"
        val videos = listOf(
            VideoUploadEntity(
                id =1,
                email = "lucas@gmail.com",
                status = "PENDING",
                createdAt = LocalDateTime.now(),
                title = "video1",
                updatedAt = LocalDateTime.now()
            ),
            VideoUploadEntity(
                id =1,
                email = "lucas@gmail.com",
                status = "PENDING",
                createdAt = LocalDateTime.now(),
                title = "video2",
                updatedAt = LocalDateTime.now()
            ),

        )

        every { videoUploadGateway.findStatusVideoByUser(email) } returns videos

        val result = findStatusVideoByUserUseCase.execute(email)

        assertEquals(2, result.size)
        assertEquals("video1", result[0].title)
        assertEquals("PENDING", result[0].status)
        assertEquals("video2", result[1].title)
        assertEquals("PENDING", result[1].status)
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia caso o usuario nao tenha nenhum upload")
    fun `should return empty list when no videos exist`() {
        val email = "user@example.com"
        every { videoUploadGateway.findStatusVideoByUser(email) } returns emptyList()

        val result = findStatusVideoByUserUseCase.execute(email)

        assertTrue(result.isEmpty())
    }
}