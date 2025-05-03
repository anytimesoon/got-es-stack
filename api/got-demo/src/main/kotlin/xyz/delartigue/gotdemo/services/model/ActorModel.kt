package xyz.delartigue.gotdemo.services.model

import xyz.delartigue.gotdemo.data.entity.ActorEntity

data class ActorModel (
    val id: Int,
    val name: String,
)

fun ActorModel.toEntity(): ActorEntity =
    ActorEntity(
        id = this.id,
        name = this.name
    )

fun ActorEntity.toModel(): ActorModel =
    ActorModel(
        id = this.id,
        name = this.name
    )