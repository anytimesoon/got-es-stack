package xyz.delartigue.gotdemo.controllers.dto

import xyz.delartigue.gotdemo.services.model.CharacterModel

data class CharacterDTO(
    val id: Int,
    val name: String,
)

fun CharacterDTO.toModel() =
    CharacterModel(
        id = id,
        name = name,
    )

fun CharacterModel.toDTO() =
    CharacterDTO(
        id = id,
        name = name,
    )