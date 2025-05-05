package main

import (
	"github.com/foam-got/consumer/got-consumer/models"
)

func newDocData(existingDoc models.EsRes, msg models.MQ) models.DocData {
	if existingDoc.Hits.Total.Value > 1 {
		return joinDoc(existingDoc, msg)
	}

	if existingDoc.Hits.Total.Value == 0 && msg.Payload.Op == "c" {
		return createDoc(existingDoc, msg)
	}

	if msg.Payload.Op == "d" {
		return deleteDoc(existingDoc, msg)
	}

	return updateDoc(existingDoc, msg)
}

func joinDoc(existingDoc models.EsRes, msg models.MQ) models.DocData {
	var actor, character models.SourceDoc
	for _, hit := range existingDoc.Hits.Hits {
		if hit.Source.ActorId > 0 {
			actor = hit
		}
		if hit.Source.CharacterId > 0 {
			character = hit
		}
	}

	return models.DocData{
		Action:     "join",
		ExistingId: actor.Id,
		DeleteId:   character.Id,
		Doc: models.Document{
			ActorId:       actor.Source.ActorId,
			ActorName:     actor.Source.ActorName,
			CharacterId:   character.Source.CharacterId,
			CharacterName: character.Source.CharacterName,
		},
	}
}

func updateDoc(existingDoc models.EsRes, msg models.MQ) models.DocData {
	doc := newDoc(existingDoc, msg)

	return models.DocData{
		Action:     "update",
		ExistingId: existingDoc.Hits.Hits[0].Id,
		Doc:        doc,
	}
}

func deleteDoc(existingDoc models.EsRes, msg models.MQ) models.DocData {
	doc := newDoc(existingDoc, msg)

	if doc.ActorName == "" || doc.CharacterName == "" {
		var id string

		if len(existingDoc.Hits.Hits) == 0 {
			id = ""
		} else {
			id = existingDoc.Hits.Hits[0].Id
		}

		return models.DocData{
			Action:     "delete",
			ExistingId: id,
			Doc:        doc,
		}
	}

	switch msg.Payload.Source.Table {
	case "character":
		return models.DocData{
			Action:     "update",
			ExistingId: existingDoc.Hits.Hits[0].Id,
			Doc: models.Document{
				ActorId:   doc.ActorId,
				ActorName: doc.ActorName,
			},
		}
	default:
		return models.DocData{
			Action:     "update",
			ExistingId: existingDoc.Hits.Hits[0].Id,
			Doc: models.Document{
				CharacterId:   doc.CharacterId,
				CharacterName: doc.CharacterName,
			},
		}
	}
}

func createDoc(existingDoc models.EsRes, msg models.MQ) models.DocData {
	doc := newDoc(existingDoc, msg)

	return models.DocData{
		Action:     "create",
		ExistingId: "",
		Doc:        doc,
	}
}

func newDoc(existingDoc models.EsRes, msg models.MQ) models.Document {
	var source models.Document
	affectedTable := msg.Payload.Source.Table

	if len(existingDoc.Hits.Hits) == 0 {
		source = models.Document{
			ActorId:       0,
			ActorName:     "",
			CharacterId:   0,
			CharacterName: "",
		}
	} else {
		source = existingDoc.Hits.Hits[0].Source
	}

	switch affectedTable {
	case "character":
		return models.Document{
			ActorId:       source.ActorId,
			ActorName:     source.ActorName,
			CharacterId:   msg.Payload.After.Id,
			CharacterName: msg.Payload.After.Name,
		}
	default:
		return models.Document{
			ActorId:       msg.Payload.After.Id,
			ActorName:     msg.Payload.After.Name,
			CharacterId:   source.CharacterId,
			CharacterName: source.CharacterName,
		}
	}
}
