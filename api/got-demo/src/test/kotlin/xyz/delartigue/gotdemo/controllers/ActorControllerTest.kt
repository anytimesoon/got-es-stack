package xyz.delartigue.gotdemo.controllers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import xyz.delartigue.gotdemo.controllers.dto.ActorDTO
import xyz.delartigue.gotdemo.services.ActorService
import xyz.delartigue.gotdemo.services.model.ActorModel
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
            ActorModel(1, "Keanu Reeves"),
            ActorModel(2, "Martin Daruss")
        )
        val expectedDTOs = listOf(
            ActorDTO(1, "Keanu Reeves"),
            ActorDTO(2, "Martin Daruss")
        )
        given(actorService.getActors()).willReturn(mockedModels)

        val result = controller.getActors()

        assertEquals(expectedDTOs, result)
    }

    @Test
    fun `createActor creates a new actor and returns it with CREATED status`() {
        val inputDTO = ActorDTO(1, "Keanu Reeves")
        val inputModel = ActorModel(1, "Keanu Reeves")
        val createdModel = ActorModel(1, "Keanu Reeves")
        val expectedDTO = ActorDTO(1, "Keanu Reeves")

        given(actorService.createActor(inputModel)).willReturn(createdModel)

        val response = controller.createActor(inputDTO)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `getActorById returns actor with OK status when found`() {
        val actorId = 1
        val foundModel = ActorModel(actorId, "Keanu Reeves")
        val expectedDTO = ActorDTO(actorId, "Keanu Reeves")

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
        val inputDTO = ActorDTO(1, "Keanu Reeves Updated")
        val inputModel = ActorModel(1, "Keanu Reeves Updated")
        val updatedModel = ActorModel(1, "Keanu Reeves Updated")
        val expectedDTO = ActorDTO(1, "Keanu Reeves Updated")

        given(actorService.updateActor(inputModel)).willReturn(updatedModel)

        val response = controller.updateActor(inputDTO)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `updateActor returns NOT_FOUND status when actor not found`() {
        val inputDTO = ActorDTO(999, "Nonexistent Actor")
        val inputModel = ActorModel(999, "Nonexistent Actor")

        given(actorService.updateActor(inputModel)).willReturn(null)

        val response = controller.updateActor(inputDTO)

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
