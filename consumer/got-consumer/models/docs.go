package models

type DocData struct {
	Action     string
	ExistingId string
	Doc        Document
}
type Document struct {
	ActorId       int    `json:"actor_id"`
	ActorName     string `json:"actor_name"`
	CharacterId   int    `json:"character_id"`
	CharacterName string `json:"character_name"`
}
