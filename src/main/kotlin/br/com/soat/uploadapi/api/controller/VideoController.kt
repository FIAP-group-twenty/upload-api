package br.com.soat.uploadapi.api.controller

import br.com.soat.uploadapi.api.config.JwtUtil
import br.com.soat.uploadapi.core.entities.FindStatusVideo
import br.com.soat.uploadapi.core.entities.VideoUrlDownload
import br.com.soat.uploadapi.core.usecase.DownloadImagesUseCase
import br.com.soat.uploadapi.core.usecase.FindStatusVideoByUserUseCase
import br.com.soat.uploadapi.core.usecase.UploadVideoUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/videos")
class VideoController(
    private val uploadVideoUseCase: UploadVideoUseCase,
    private val jwtUtil: JwtUtil,
    private val findStatusVideoByUserUseCase: FindStatusVideoByUserUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase
) {

    @PostMapping
    fun uploadVideos(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
//        val token = authHeader.removePrefix("Bearer ")
//        val email = jwtUtil.extractEmail(token) ?: throw RuntimeException("Email is missing")
        uploadVideoUseCase.execute("lucassouzahd@gmail.com", file)
        return ResponseEntity.accepted().body("video ${file.originalFilename} sent for processing")
    }

    @GetMapping
    fun findStatusVideoByUser(@RequestHeader("Authorization") authHeader: String): ResponseEntity<MutableList<FindStatusVideo>> {
//        val token = authHeader.removePrefix("Bearer ")
//        val email = jwtUtil.extractEmail(token) ?: throw RuntimeException("Email is missing")
        return ResponseEntity.ok(findStatusVideoByUserUseCase.execute("lucassouzahd@gmail.com"))
    }

    @GetMapping("download")
    fun downloadImages(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam("file") fileName: String
    ): ResponseEntity<VideoUrlDownload> {
        //val token = authHeader.removePrefix("Bearer ")
        //val email = jwtUtil.extractEmail(token) ?: throw RuntimeException("Email is missing")
        return ResponseEntity.ok(
            VideoUrlDownload(downloadImagesUseCase.execute("lucassouzahd@gmail.com", fileName))
        )
    }

}