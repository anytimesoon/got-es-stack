package xyz.delartigue.gotdemo.services

import org.springframework.stereotype.Service
import xyz.delartigue.gotdemo.data.CharacterRepository
import xyz.delartigue.gotdemo.services.model.CharacterModel
import xyz.delartigue.gotdemo.services.model.toEntity
import xyz.delartigue.gotdemo.services.model.toModel
import kotlin.jvm.optionals.getOrNull

@Service
class CharacterService (
    private val characterRepository: CharacterRepository
) {
    fun getCharacters() = characterRepository.findAll().map { it.toModel()}

    fun createCharacter(character: CharacterModel) = characterRepository.save(character.toEntity().copy(id = null)).toModel()

    fun getCharacterById(id: Int): CharacterModel? {
        val character = characterRepository.findById(id)

        return character.getOrNull()?.toModel()
    }

    fun updateCharacter(character: CharacterModel): CharacterModel? {
        val existingCharacter = characterRepository.findById(character.id).getOrNull()?.toModel()

        if (existingCharacter == null) return null

        return characterRepository.save(
            existingCharacter.copy(
                name = character.name,
            ).toEntity()
        ).toModel()
    }

    fun deleteCharacterById(id: Int): Boolean {
        val character = characterRepository.findById(id).getOrNull()
        if (character == null) {
            return false
        }
        characterRepository.delete(character)
        return true
    }
}