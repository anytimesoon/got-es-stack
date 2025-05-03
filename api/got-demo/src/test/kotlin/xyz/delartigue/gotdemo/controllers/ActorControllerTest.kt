package xyz.delartigue.gotdemo.controllers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import xyz.delartigue.gotdemo.controllers.dto.ActorDTO
import xyz.delartigue.gotdemo.controllers.dto.CharacterDTO
import xyz.delartigue.gotdemo.services.ActorService
import xyz.delartigue.gotdemo.services.model.ActorModel
import xyz.delartigue.gotdemo.services.model.CharacterModel
import kotlin.test.BeforeTest

class ActorControllerTest {

    @Mock
    private lateinit var actorService: ActorService
    private lateinit var controller: ActorController

    @BeforeTest
    fun setUp() {
        actorService = mock()
        controller = ActorController(actorService)
    }

    @Test
    fun `getActors returns all actors as DTOs`() {
        val mockedModels = listOf(
            ActorModel(1, "Keanu Reeves", null),
            ActorModel(2, "Martin Daruss", null)
        )
        val expectedDTOs = listOf(
            ActorDTO(1, "Keanu Reeves", null),
            ActorDTO(2, "Martin Daruss", null)
        )
        given(actorService.getActors()).willReturn(mockedModels)

        val result = controller.getActors()

        assertEquals(expectedDTOs, result)
    }

    @Test
    fun `createActor creates a new actor and returns it with CREATED status`() {
        val inputDTO = ActorDTO(1, "Keanu Reeves", null)
        val inputModel = ActorModel(1, "Keanu Reeves", null)
        val createdModel = ActorModel(1, "Keanu Reeves", null)
        val expectedDTO = ActorDTO(1, "Keanu Reeves", null)

        given(actorService.createActor(inputModel)).willReturn(createdModel)

        val response = controller.createActor(inputDTO)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `getActorById returns actor with OK status when found`() {
        val actorId = 1
        val foundModel = ActorModel(actorId, "Keanu Reeves", null)
        val expectedDTO = ActorDTO(actorId, "Keanu Reeves", null)

        given(actorService.getActorById(actorId)).willReturn(foundModel)

        val response = controller.getActorById(actorId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `getActorById returns NOT_FOUND status when actor not found`() {
        val actorId = 1

        given(actorService.getActorById(actorId)).willReturn(null)

        val response = controller.getActorById(actorId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `updateActor returns updated actor with OK status when found`() {
        val inputDTO = ActorDTO(1, "Keanu Reeves Updated", null)
        val inputModel = ActorModel(1, "Keanu Reeves Updated", null)
        val updatedModel = ActorModel(1, "Keanu Reeves Updated", null)
        val expectedDTO = ActorDTO(1, "Keanu Reeves Updated", null)

        given(actorService.updateActor(inputModel)).willReturn(updatedModel)

        val response = controller.updateActor(inputDTO)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `updateActor returns NOT_FOUND status when actor not found`() {
        val inputDTO = ActorDTO(999, "Nonexistent Actor", null)
        val inputModel = ActorModel(999, "Nonexistent Actor", null)

        given(actorService.updateActor(inputModel)).willReturn(null)

        val response = controller.updateActor(inputDTO)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `linkActorToCharacter returns updated actor with OK status when found`() {
        given(actorService.linkActorToCharacter(1, 1)).willReturn(ActorModel(1, "Keanu Reeves", CharacterModel(1, "Neo")))

        val response = controller.linkActorToCharacter(1, 1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(ActorDTO(1, "Keanu Reeves", CharacterDTO(1, "Neo")), response.body)
    }

    @Test
    fun `linkActorToCharacter returns NOT_FOUND status when actor or character not found`() {
        given(actorService.linkActorToCharacter(999, 999)).willReturn(null)

        val response = controller.linkActorToCharacter(999, 999)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `deleteActor returns NO_CONTENT status when actor is deleted`() {
        val actorId = 1

        given(actorService.deleteActorById(actorId)).willReturn(true)

        val response = controller.deleteActor(actorId)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `deleteActor returns NOT_FOUND status when actor not found`() {
        val actorId = 1

        given(actorService.deleteActorById(actorId)).willReturn(false)

        val response = controller.deleteActor(actorId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

}
