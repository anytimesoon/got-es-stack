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
import xyz.delartigue.gotdemo.controllers.dto.ActorDTO
import xyz.delartigue.gotdemo.controllers.dto.toDTO
import xyz.delartigue.gotdemo.controllers.dto.toModel
import xyz.delartigue.gotdemo.services.ActorService

@RestController
@RequestMapping("/actors")
class ActorController (
    private val actorService: ActorService
) {
    @GetMapping("")
    fun getActors(): List<ActorDTO> = actorService.getActors().map { it.toDTO() }

    @PostMapping("")
    fun createActor(
        @RequestBody actorDTO: ActorDTO
    ): ResponseEntity<ActorDTO> =
        ResponseEntity(
            actorService.createActor(actorDTO.toModel()).toDTO(),
            HttpStatus.CREATED
        )

    @GetMapping("/{id}")
    fun getActorById(
        @PathVariable("id") id: Int
    ): ResponseEntity<ActorDTO> {
        val actor = actorService.getActorById(id)?.toDTO()

        if (actor == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(actor, HttpStatus.OK)
    }

    @PutMapping("")
    fun updateActor(
        @RequestBody actorDTO: ActorDTO
    ): ResponseEntity<ActorDTO> {
        val actor = actorService.updateActor(actorDTO.toModel())?.toDTO()

        if (actor == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(actor, HttpStatus.OK)
    }

    @PutMapping("/{actor_id}/link_to/{character_id}")
    fun linkActorToCharacter(
        @PathVariable("actor_id") actorId: Int,
        @PathVariable("character_id") characterId: Int
    ): ResponseEntity<ActorDTO> {
        val actor = actorService.linkActorToCharacter(actorId, characterId)?.toDTO()

        if (actor == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(actor, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteActor(
        @PathVariable("id") id: Int
    ): ResponseEntity<ActorDTO> {
        return when(actorService.deleteActorById(id)) {
            true -> ResponseEntity(HttpStatus.NO_CONTENT)
            false -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}