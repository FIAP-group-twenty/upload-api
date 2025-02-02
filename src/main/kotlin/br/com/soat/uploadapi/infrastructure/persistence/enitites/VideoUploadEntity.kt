package br.com.soat.uploadapi.infrastructure.persistence.enitites

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "tb_video_upload")
data class VideoUploadEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotBlank(message = "idUser is required")
    @Column(name = "email")
    @Email(message = "deve ter o formato valido")
    var email: String,

    @NotBlank(message = "title is required")
    @Column(name = "title")
    var title: String,

    @Column(name = "url_video")
    var urlVideo: String? = null,

    @Column(name = "url_zip_image")
    var urlZipImages: String? = null,

    @Column(name = "status")
    var status: String,

    @Column(name = "created_at")
    var createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime,
)