package br.com.soat.uploadapi.api.controller

import br.com.soat.uploadapi.core.usecase.UploadVideoUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/videos")
class VideoUploadController(
    private val uploadVideoUseCase: UploadVideoUseCase
) {

    @PostMapping
    fun uploadVideos(
        @RequestParam("userId") userId: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        val fileName = file.originalFilename ?: "$userId"
        val content = file.contentType ?: "application/octet-stream"

        file.inputStream.use {
            uploadVideoUseCase.execute(userId, fileName, it, content)
            return ResponseEntity.ok("File uploaded")
        }
    }

}