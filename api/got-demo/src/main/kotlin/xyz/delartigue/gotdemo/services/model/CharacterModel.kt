package xyz.delartigue.gotdemo.services.model

import xyz.delartigue.gotdemo.data.entity.CharacterEntity

data class CharacterModel (
    val id: Int,
    val name: String,
)

fun CharacterModel.toEntity(): CharacterEntity =
    CharacterEntity(
        id = this.id,
        name = this.name
    )

fun CharacterEntity.toModel(): CharacterModel =
    CharacterModel(
        id = this.id!!,
        name = this.name
    )