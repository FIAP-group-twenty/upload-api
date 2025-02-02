package br.com.soat.uploadapi.api.controller

import br.com.soat.uploadapi.api.config.JwtUtil
import br.com.soat.uploadapi.core.usecase.UploadVideoUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/videos")
class VideoUploadController(
    private val uploadVideoUseCase: UploadVideoUseCase,
    private val jwtUtil: JwtUtil,
) {

    @PostMapping
    fun uploadVideos(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        val token = authHeader.removePrefix("Bearer ")
        val email = jwtUtil.extractEmail(token) ?: throw RuntimeException("Email is missing")
        uploadVideoUseCase.execute(email, file)
        return ResponseEntity.accepted().body("video ${file.originalFilename} sent for processing")
    }
}