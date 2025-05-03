package xyz.delartigue.gotdemo.services.model

import xyz.delartigue.gotdemo.data.entity.ActorEntity

data class ActorModel (
    val id: Int,
    val name: String,
    val character: CharacterModel?
)

fun ActorModel.toEntity(): ActorEntity =
    ActorEntity(
        id = this.id,
        name = this.name,
        character = this.character?.toEntity()
    )

fun ActorEntity.toModel(): ActorModel =
    ActorModel(
        id = this.id,
        name = this.name,
        character = this.character?.toModel()
    )