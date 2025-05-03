package xyz.delartigue.gotdemo.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import xyz.delartigue.gotdemo.data.ActorRepository
import xyz.delartigue.gotdemo.data.entity.ActorEntity
import xyz.delartigue.gotdemo.services.model.ActorModel
import java.util.Optional
import kotlin.test.BeforeTest

class ActorServiceTest {

    @Mock
    private lateinit var actorRepository: ActorRepository
    private lateinit var service: ActorService

    @BeforeTest
    fun setUp() {
        actorRepository = mock()
        service = ActorService(actorRepository)
    }

    @Test
    fun `get actors returns all actors`() {
        val mockedEntity = listOf(
            ActorEntity(1, "Keanu Reeves"),
            ActorEntity(2, "Martin Daruss"),
        )
        val expectedResult = listOf(
            ActorModel(1, "Keanu Reeves"),
            ActorModel(2, "Martin Daruss"),
        )
        given(actorRepository.findAll()).willReturn(mockedEntity)

        val actors = service.getActors()

        assertEquals(expectedResult, actors)
    }


    @Test
    fun `get actors can return an empty list`() {
        val mockedEntity = listOf<ActorEntity>()
        val expectedResult = listOf<ActorModel>()
        given(actorRepository.findAll()).willReturn(mockedEntity)

        val actors = service.getActors()

        assertEquals(expectedResult, actors)
    }

    @Test
    fun `create actor creates a new actor`() {
        val mockedEntity = ActorEntity(1, "Keanu Reeves")
        val expectedResult = ActorModel(1, "Keanu Reeves")
        given(actorRepository.save(mockedEntity)).willReturn(mockedEntity)

        val createdActor = service.createActor(ActorModel(1, "Keanu Reeves"))

        assertEquals(expectedResult, createdActor)
    }

    @Test
    fun `get actor by id returns an existing actor`() {
        val mockedEntity = Optional.of(ActorEntity(1, "Keanu Reeves"))
        val expectedResult = ActorModel(1, "Keanu Reeves")
        given(actorRepository.findById(1)).willReturn(mockedEntity)

        val actor = service.getActorById(1)

        assertEquals(expectedResult, actor)
    }


    @Test
    fun `get actor by id returns null if no actor is found`() {
        val mockedEntity = Optional.empty<ActorEntity>()
        given(actorRepository.findById(1)).willReturn(mockedEntity)

        val actor = service.getActorById(1)

        assertNull(actor)
    }

    @Test
    fun `update actor updates an actor`() {
        val mockedEntity = Optional.of(ActorEntity(1, "Keanu Reeves"))
        val mockedSavedEntity = Optional.of(ActorEntity(1, "Keanu Reeves!"))
        val expectedResult = ActorModel(1, "Keanu Reeves!")
        given(actorRepository.findById(1)).willReturn(mockedEntity)
        given(actorRepository.save(mockedSavedEntity.get())).willReturn(mockedSavedEntity.get())

        val actor = service.updateActor(expectedResult)

        assertEquals(expectedResult, actor)
    }


    @Test
    fun `update actor returns null if actor not found`() {
        val mockedEntity = Optional.empty<ActorEntity>()
        given(actorRepository.findById(1)).willReturn(mockedEntity)

        val actor = service.updateActor(ActorModel(1, "Keanu Reeves!"))

        assertNull(actor)
    }

    @Test
    fun `delete actor by id deletes and actor`() {
        val mockedEntity = Optional.of(ActorEntity(1, "Keanu Reeves"))
        given(actorRepository.findById(1)).willReturn(mockedEntity)

        val isDeleted = service.deleteActorById(1)

        assertTrue(isDeleted)
    }

    @Test
    fun `delete actor by id returns false if actor is not deleted`() {
        val mockedEntity = Optional.empty<ActorEntity>()
        given(actorRepository.findById(1)).willReturn(mockedEntity)

        val isDeleted = service.deleteActorById(1)

        assertFalse(isDeleted)
    }
}