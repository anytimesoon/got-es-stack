package xyz.delartigue.gotdemo.data.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "actor")
data class ActorEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val name: String,
    @OneToOne(mappedBy = "actor")
    @JoinColumn(name = "character_id", referencedColumnName = "id")
    val character: CharacterEntity?
)