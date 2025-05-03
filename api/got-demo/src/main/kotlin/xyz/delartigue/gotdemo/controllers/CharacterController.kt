package xyz.delartigue.gotdemo.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.delartigue.gotdemo.controllers.dto.CharacterDTO
import xyz.delartigue.gotdemo.controllers.dto.toDTO
import xyz.delartigue.gotdemo.controllers.dto.toModel
import xyz.delartigue.gotdemo.services.CharacterService

@RestController
@RequestMapping("/characters")
class CharacterController (
    private val characterService: CharacterService
) {
    @GetMapping("")
    fun getCharacters(): List<CharacterDTO> = characterService.getCharacters().map { it.toDTO() }

    @PostMapping("")
    fun createCharacter(
        @RequestBody characterDTO: CharacterDTO
    ): ResponseEntity<CharacterDTO> =
        ResponseEntity(
            characterService.createCharacter(characterDTO.toModel()).toDTO(),
            HttpStatus.CREATED
        )

    @GetMapping("/{id}")
    fun getCharacterById(
        @PathVariable("id") id: Int
    ): ResponseEntity<CharacterDTO> {
        val character = characterService.getCharacterById(id)?.toDTO()

        if (character == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(character, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateCharacter(
        @RequestBody characterDTO: CharacterDTO
    ): ResponseEntity<CharacterDTO> {
        val character = characterService.updateCharacter(characterDTO.toModel())?.toDTO()

        if (character == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(character, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteCharacter(
        @PathVariable("id") id: Int
    ): ResponseEntity<CharacterDTO> {
        return when(characterService.deleteCharacterById(id)) {
            true -> ResponseEntity(HttpStatus.NO_CONTENT)
            false -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}