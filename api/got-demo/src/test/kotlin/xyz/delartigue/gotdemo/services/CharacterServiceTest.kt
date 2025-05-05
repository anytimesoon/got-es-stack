package xyz.delartigue.gotdemo.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import xyz.delartigue.gotdemo.data.CharacterRepository
import xyz.delartigue.gotdemo.data.entity.CharacterEntity
import xyz.delartigue.gotdemo.services.model.CharacterModel
import java.util.Optional
import kotlin.test.BeforeTest

class CharacterServiceTest {

    @Mock
    private lateinit var characterRepository: CharacterRepository
    private lateinit var service: CharacterService

    @BeforeTest
    fun setUp() {
        characterRepository = mock()
        service = CharacterService(characterRepository)
    }

    @Test
    fun `get characters returns all characters`() {
        val mockedEntity = listOf(
            CharacterEntity(1, "Keanu Reeves"),
            CharacterEntity(2, "Martin Daruss"),
        )
        val expectedResult = listOf(
            CharacterModel(1, "Keanu Reeves"),
            CharacterModel(2, "Martin Daruss"),
        )
        given(characterRepository.findAll()).willReturn(mockedEntity)

        val characters = service.getCharacters()

        assertEquals(expectedResult, characters)
    }


    @Test
    fun `get characters can return an empty list`() {
        val mockedEntity = listOf<CharacterEntity>()
        val expectedResult = listOf<CharacterModel>()
        given(characterRepository.findAll()).willReturn(mockedEntity)

        val characters = service.getCharacters()

        assertEquals(expectedResult, characters)
    }

    @Test
    fun `create character creates a new character`() {
        val mockedEntity = CharacterEntity(1, "Keanu Reeves")
        val expectedResult = CharacterModel(1, "Keanu Reeves")
        given(characterRepository.save(mockedEntity.copy(id = null))).willReturn(mockedEntity)

        val createdCharacter = service.createCharacter(CharacterModel(1, "Keanu Reeves"))

        assertEquals(expectedResult, createdCharacter)
    }

    @Test
    fun `get character by id returns an existing character`() {
        val mockedEntity = Optional.of(CharacterEntity(1, "Keanu Reeves"))
        val expectedResult = CharacterModel(1, "Keanu Reeves")
        given(characterRepository.findById(1)).willReturn(mockedEntity)

        val character = service.getCharacterById(1)

        assertEquals(expectedResult, character)
    }


    @Test
    fun `get character by id returns null if no character is found`() {
        val mockedEntity = Optional.empty<CharacterEntity>()
        given(characterRepository.findById(1)).willReturn(mockedEntity)

        val character = service.getCharacterById(1)

        assertNull(character)
    }

    @Test
    fun `update character updates an character`() {
        val mockedEntity = Optional.of(CharacterEntity(1, "Keanu Reeves"))
        val mockedSavedEntity = Optional.of(CharacterEntity(1, "Keanu Reeves!"))
        val expectedResult = CharacterModel(1, "Keanu Reeves!")
        given(characterRepository.findById(1)).willReturn(mockedEntity)
        given(characterRepository.save(mockedSavedEntity.get())).willReturn(mockedSavedEntity.get())

        val character = service.updateCharacter(expectedResult)

        assertEquals(expectedResult, character)
    }


    @Test
    fun `update character returns null if character not found`() {
        val mockedEntity = Optional.empty<CharacterEntity>()
        given(characterRepository.findById(1)).willReturn(mockedEntity)

        val character = service.updateCharacter(CharacterModel(1, "Keanu Reeves!"))

        assertNull(character)
    }

    @Test
    fun `delete character by id deletes and character`() {
        val mockedEntity = Optional.of(CharacterEntity(1, "Keanu Reeves"))
        given(characterRepository.findById(1)).willReturn(mockedEntity)

        val isDeleted = service.deleteCharacterById(1)

        assertTrue(isDeleted)
    }

    @Test
    fun `delete character by id returns false if character is not deleted`() {
        val mockedEntity = Optional.empty<CharacterEntity>()
        given(characterRepository.findById(1)).willReturn(mockedEntity)

        val isDeleted = service.deleteCharacterById(1)

        assertFalse(isDeleted)
    }
}