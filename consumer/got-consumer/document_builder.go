package main

import (
	"github.com/foam-got/consumer/got-consumer/models"
)

func newDocData(existingDoc models.EsRes, msg models.MQ) models.DocData {
	if existingDoc.Hits.Total.Value == 0 {
		return createDoc(existingDoc, msg)
	}

	if msg.Payload.Op == "d" {
		return deleteDoc(existingDoc, msg)
	}

	return updateDoc(existingDoc, msg)
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
		return models.DocData{
			Action:     "delete",
			ExistingId: existingDoc.Hits.Hits[0].Id,
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
