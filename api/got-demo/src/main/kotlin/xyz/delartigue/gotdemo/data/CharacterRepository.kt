package xyz.delartigue.gotdemo.data

import org.springframework.data.repository.CrudRepository
import xyz.delartigue.gotdemo.data.entity.CharacterEntity

interface CharacterRepository : CrudRepository<CharacterEntity, Int>
