package br.com.soat.uploadapi.core.usecase

import br.com.soat.uploadapi.core.entities.FindStatusVideo
import br.com.soat.uploadapi.core.gateways.IVideoGateway

class FindStatusVideoByUserUseCase(
    private val videoUploadGateway: IVideoGateway
) {

    fun execute(email: String): MutableList<FindStatusVideo> {
        val listVideos = videoUploadGateway.findStatusVideoByUser(email)
        return listVideos.map { v -> FindStatusVideo(v?.title, v?.status) }.toMutableList()
    }

}