package xyz.delartigue.gotdemo.data

import org.springframework.data.repository.CrudRepository
import xyz.delartigue.gotdemo.data.entity.ActorEntity

interface ActorRepository : CrudRepository<ActorEntity, Int>
