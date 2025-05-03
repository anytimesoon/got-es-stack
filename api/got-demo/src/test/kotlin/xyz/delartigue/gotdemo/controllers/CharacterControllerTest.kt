package xyz.delartigue.gotdemo.controllers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import xyz.delartigue.gotdemo.controllers.dto.CharacterDTO
import xyz.delartigue.gotdemo.services.CharacterService
import xyz.delartigue.gotdemo.services.model.CharacterModel
import kotlin.test.BeforeTest

class CharacterControllerTest {

    @Mock
    private lateinit var characterService: CharacterService
    private lateinit var controller: CharacterController

    @BeforeTest
    fun setUp() {
        characterService = mock()
        controller = CharacterController(characterService)
    }

    @Test
    fun `getCharacters returns all characters as DTOs`() {
        val mockedModels = listOf(
            CharacterModel(1, "Keanu Reeves"),
            CharacterModel(2, "Martin Daruss")
        )
        val expectedDTOs = listOf(
            CharacterDTO(1, "Keanu Reeves"),
            CharacterDTO(2, "Martin Daruss")
        )
        given(characterService.getCharacters()).willReturn(mockedModels)

        val result = controller.getCharacters()

        assertEquals(expectedDTOs, result)
    }

    @Test
    fun `createCharacter creates a new character and returns it with CREATED status`() {
        val inputDTO = CharacterDTO(1, "Keanu Reeves")
        val inputModel = CharacterModel(1, "Keanu Reeves")
        val createdModel = CharacterModel(1, "Keanu Reeves")
        val expectedDTO = CharacterDTO(1, "Keanu Reeves")

        given(characterService.createCharacter(inputModel)).willReturn(createdModel)

        val response = controller.createCharacter(inputDTO)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `getCharacterById returns character with OK status when found`() {
        val characterId = 1
        val foundModel = CharacterModel(characterId, "Keanu Reeves")
        val expectedDTO = CharacterDTO(characterId, "Keanu Reeves")

        given(characterService.getCharacterById(characterId)).willReturn(foundModel)

        val response = controller.getCharacterById(characterId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `getCharacterById returns NOT_FOUND status when character not found`() {
        val characterId = 1

        given(characterService.getCharacterById(characterId)).willReturn(null)

        val response = controller.getCharacterById(characterId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `updateCharacter returns updated character with OK status when found`() {
        val inputDTO = CharacterDTO(1, "Keanu Reeves Updated")
        val inputModel = CharacterModel(1, "Keanu Reeves Updated")
        val updatedModel = CharacterModel(1, "Keanu Reeves Updated")
        val expectedDTO = CharacterDTO(1, "Keanu Reeves Updated")

        given(characterService.updateCharacter(inputModel)).willReturn(updatedModel)

        val response = controller.updateCharacter(inputDTO)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDTO, response.body)
    }

    @Test
    fun `updateCharacter returns NOT_FOUND status when character not found`() {
        val inputDTO = CharacterDTO(999, "Nonexistent Character")
        val inputModel = CharacterModel(999, "Nonexistent Character")

        given(characterService.updateCharacter(inputModel)).willReturn(null)

        val response = controller.updateCharacter(inputDTO)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `deleteCharacter returns NO_CONTENT status when character is deleted`() {
        val characterId = 1

        given(characterService.deleteCharacterById(characterId)).willReturn(true)

        val response = controller.deleteCharacter(characterId)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun `deleteCharacter returns NOT_FOUND status when character not found`() {
        val characterId = 1

        given(characterService.deleteCharacterById(characterId)).willReturn(false)

        val response = controller.deleteCharacter(characterId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

}
