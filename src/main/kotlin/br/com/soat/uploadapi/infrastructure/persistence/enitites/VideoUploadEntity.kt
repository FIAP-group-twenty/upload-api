package br.com.soat.uploadapi.infrastructure.persistence.enitites

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "tb_video_upload")
data class VideoUploadEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @NotBlank(message = "idUser is required")
    @Column(name = "id_user")
    val idUser: Long,

    @NotBlank(message = "title is required")
    @Column(name = "title")
    val title: String,

    @Column(name = "url_video")
    val urlVideo: String,

    @Column(name = "url_zip_image")
    val urlZipImages: String? = null,

    @Column(name = "status")
    val status: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime,
)