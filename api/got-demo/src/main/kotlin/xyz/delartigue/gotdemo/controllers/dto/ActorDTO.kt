package xyz.delartigue.gotdemo.controllers.dto

import xyz.delartigue.gotdemo.services.model.ActorModel

data class ActorDTO(
    val id: Int,
    val name: String,
)

fun ActorDTO.toModel() =
    ActorModel(
        id = id,
        name = name,
    )

fun ActorModel.toDTO() =
    ActorDTO(
        id = id,
        name = name,
    )