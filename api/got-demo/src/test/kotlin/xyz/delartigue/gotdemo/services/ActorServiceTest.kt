package xyz.delartigue.gotdemo.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import xyz.delartigue.gotdemo.data.ActorRepository
import xyz.delartigue.gotdemo.data.CharacterRepository
import xyz.delartigue.gotdemo.data.entity.ActorEntity
import xyz.delartigue.gotdemo.data.entity.CharacterEntity
import xyz.delartigue.gotdemo.services.model.ActorModel
import xyz.delartigue.gotdemo.services.model.CharacterModel
import java.util.Optional
import kotlin.test.BeforeTest

class ActorServiceTest {

    @Mock
    private lateinit var actorRepository: ActorRepository
    @Mock
    private lateinit var characterRepository: CharacterRepository
    private lateinit var service: ActorService

    @BeforeTest
    fun setUp() {
        actorRepository = mock()
        characterRepository = mock()
        service = ActorService(actorRepository, characterRepository)
    }

    @Test
    fun `get actors returns all actors`() {
        val mockedEntity = listOf(
            ActorEntity(1, "Keanu Reeves", null),
            ActorEntity(2, "Martin Daruss", null),
        )
        val expectedResult = listOf(
            ActorModel(1, "Keanu Reeves", null),
            ActorModel(2, "Martin Daruss", null),
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
        val mockedEntity = ActorEntity(1, "Keanu Reeves", null)
        val expectedResult = ActorModel(1, "Keanu Reeves", null)
        given(actorRepository.save(mockedEntity.copy(id = null))).willReturn(mockedEntity)

        val createdActor = service.createActor(ActorModel(1, "Keanu Reeves", null))

        assertEquals(expectedResult, createdActor)
    }

    @Test
    fun `get actor by id returns an existing actor`() {
        val mockedEntity = Optional.of(ActorEntity(1, "Keanu Reeves", null))
        val expectedResult = ActorModel(1, "Keanu Reeves", null)
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
        val mockedEntity = Optional.of(ActorEntity(1, "Keanu Reeves", null))
        val mockedSavedEntity = Optional.of(ActorEntity(1, "Keanu Reeves Updated", null))
        val expectedResult = ActorModel(1, "Keanu Reeves Updated", null)
        given(actorRepository.findById(1)).willReturn(mockedEntity)
        given(actorRepository.save(mockedSavedEntity.get())).willReturn(mockedSavedEntity.get())

        val actor = service.updateActor(expectedResult)

        assertEquals(expectedResult, actor)
    }

    @Test
    fun `update actor returns null if actor not found`() {
        val mockedEntity = Optional.empty<ActorEntity>()
        given(actorRepository.findById(1)).willReturn(mockedEntity)

        val actor = service.updateActor(ActorModel(1, "Keanu Reeves Updated", null))

        assertNull(actor)
    }

    @Test
    fun `link actor to character updates the actor`() {
        val expectedActor = ActorModel(1, "Keanu Reeves", CharacterModel(1, "Neo"))
        val mockedActorEntity = Optional.of(ActorEntity(1, "Keanu Reeves", null))
        val mockedCharacterEntity = Optional.of(CharacterEntity(1, "Neo"))
        val expectedSavedEntity = ActorEntity(1, "Keanu Reeves", CharacterEntity(1, "Neo"))

        given(actorRepository.findById(1)).willReturn(mockedActorEntity)
        given(characterRepository.findById(1)).willReturn(mockedCharacterEntity)
        given(actorRepository.save(expectedSavedEntity)).willReturn(expectedSavedEntity)

        val actor = service.linkActorToCharacter(1, 1)

        assertEquals(expectedActor, actor)

    }

    @Test
    fun `link actor to character null if actor not found`() {
        val mockedEntity = Optional.empty<ActorEntity>()
        given(actorRepository.findById(1)).willReturn(mockedEntity)

        val actor = service.linkActorToCharacter(1, 1)

        assertNull(actor)
    }

    @Test
    fun `link actor to character null if character not found`() {
        val mockedCharacterEntity = Optional.empty<CharacterEntity>()
        val mockedActorEntity = Optional.of(ActorEntity(1, "Keanu Reeves", null))
        given(actorRepository.findById(1)).willReturn(mockedActorEntity)
        given(characterRepository.findById(1)).willReturn(mockedCharacterEntity)

        val actor = service.linkActorToCharacter(1, 1)

        assertNull(actor)
    }

    @Test
    fun `delete actor by id deletes and actor`() {
        val mockedEntity = Optional.of(ActorEntity(1, "Keanu Reeves", null))
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