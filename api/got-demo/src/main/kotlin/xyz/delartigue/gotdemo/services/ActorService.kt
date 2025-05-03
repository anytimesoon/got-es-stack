package xyz.delartigue.gotdemo.services

import org.springframework.stereotype.Service
import xyz.delartigue.gotdemo.data.ActorRepository
import xyz.delartigue.gotdemo.data.CharacterRepository
import xyz.delartigue.gotdemo.services.model.ActorModel
import xyz.delartigue.gotdemo.services.model.toEntity
import xyz.delartigue.gotdemo.services.model.toModel
import kotlin.jvm.optionals.getOrNull

@Service
class ActorService (
    private val actorRepository: ActorRepository,
    private val characterRepository: CharacterRepository
) {
    fun getActors() = actorRepository.findAll().map { it.toModel()}

    fun createActor(actor: ActorModel) = actorRepository.save(actor.toEntity()).toModel()

    fun getActorById(id: Int): ActorModel? {
        val actor = actorRepository.findById(id)

        return actor.getOrNull()?.toModel()
    }

    fun updateActor(actor: ActorModel): ActorModel? {
        val existingActor = actorRepository.findById(actor.id).getOrNull()?.toModel()

        if (existingActor == null) return null

        return actorRepository.save(
            existingActor.copy(
                name = actor.name,
            ).toEntity()
        ).toModel()
    }

    fun linkActorToCharacter(actorId: Int, characterId: Int): ActorModel? {
        val actor = actorRepository.findById(actorId).getOrNull()?.toModel()
        val character = characterRepository.findById(characterId).getOrNull()?.toModel()

        if (actor == null || character == null) return null

        return actorRepository.save(
            actor.copy(
                character = character
            ).toEntity()
        ).toModel()
    }

    fun deleteActorById(id: Int): Boolean {
        val actor = actorRepository.findById(id).getOrNull()
        if (actor == null) {
            return false
        }
        actorRepository.delete(actor)
        return true
    }
}