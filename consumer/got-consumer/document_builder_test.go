package main

import (
	"github.com/foam-got/consumer/got-consumer/models"
	"reflect"
	"testing"
)

func TestNewDoc(t *testing.T) {
	type args struct {
		existingDoc models.EsRes
		msg         models.MQ
	}
	tests := []struct {
		name string
		args args
		want models.DocData
	}{
		{
			name: "creates a document for a new character",
			args: args{
				existingDoc: generateEsRes("", 0, "", 0, "", 0),
				msg:         generateMQ("Neo", "c", "character", 0),
			},
			want: generateDocumentData("create", "", "", 0, "Neo", 0),
		},
		{
			name: "creates a document for a new actor",
			args: args{
				existingDoc: generateEsRes("", 0, "", 0, "", 0),
				msg:         generateMQ("Keanu Reeves", "c", "actor", 0),
			},
			want: generateDocumentData("create", "", "Keanu Reeves", 0, "", 0),
		},
		{
			name: "updates a document for a character",
			args: args{
				existingDoc: generateEsRes("", 0, "Neo", 1, "testid", 1),
				msg:         generateMQ("Neo Updated", "c", "character", 1),
			},
			want: generateDocumentData("update", "testid", "", 0, "Neo Updated", 1),
		},
		{
			name: "updates a document for a character with an actor name",
			args: args{
				existingDoc: generateEsRes("Keanu Reeves", 1, "Neo", 1, "testid", 1),
				msg:         generateMQ("Neo Updated", "c", "character", 1),
			},
			want: generateDocumentData("update", "testid", "Keanu Reeves", 1, "Neo Updated", 1),
		},
		{
			name: "updates a document for an actor",
			args: args{
				existingDoc: generateEsRes("Keanu Reeves", 1, "", 0, "testid", 1),
				msg:         generateMQ("Keanu Reeves Updated", "c", "actor", 1),
			},
			want: generateDocumentData("update", "testid", "Keanu Reeves Updated", 1, "", 0),
		},
		{
			name: "updates a document for an actor",
			args: args{
				existingDoc: generateEsRes("Keanu Reeves", 1, "Neo", 1, "testid", 1),
				msg:         generateMQ("Keanu Reeves Updated", "c", "actor", 1),
			},
			want: generateDocumentData("update", "testid", "Keanu Reeves Updated", 1, "Neo", 1),
		},
		{
			name: "deletes a document for a character if there is no actor name",
			args: args{
				existingDoc: generateEsRes("", 0, "Neo", 1, "testid", 1),
				msg:         generateMQ("Neo", "d", "character", 1),
			},
			want: generateDocumentData("delete", "testid", "", 0, "Neo", 1),
		},
		{
			name: "removes a character if there is an actor name",
			args: args{
				existingDoc: generateEsRes("Keanu Reeves", 1, "Neo", 1, "testid", 1),
				msg:         generateMQ("Neo", "d", "character", 1),
			},
			want: generateDocumentData("update", "testid", "Keanu Reeves", 1, "", 0),
		},
		{
			name: "deletes a document for a actor if there is no character name",
			args: args{
				existingDoc: generateEsRes("Keanu Reeves", 1, "", 0, "testid", 1),
				msg:         generateMQ("Keanu Reeves", "d", "actor", 1),
			},
			want: generateDocumentData("delete", "testid", "Keanu Reeves", 1, "", 0),
		},
		{
			name: "removes an actor if there is a character name",
			args: args{
				existingDoc: generateEsRes("Keanu Reeves", 1, "Neo", 1, "testid", 1),
				msg:         generateMQ("Keanu Reeves", "d", "actor", 1),
			},
			want: generateDocumentData("update", "testid", "", 0, "Neo", 1),
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := newDocData(tt.args.existingDoc, tt.args.msg); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("newDocData() %s = %v, want %v", tt.name, got, tt.want)
			}
		})
	}
}

func generateEsRes(actorName string, actorId int, characterName string, characterId int, id string, value int) models.EsRes {
	return models.EsRes{
		Took:     0,
		TimedOut: false,
		Shards:   nil,
		Hits: models.Hits{
			Total: models.Total{
				Value:    value,
				Relation: "eq",
			},
			MaxScore: nil,
			Hits: []models.SourceDoc{
				{
					Index: "got",
					Id:    id,
					Score: 1,
					Source: models.Document{
						ActorId:       actorId,
						ActorName:     actorName,
						CharacterId:   characterId,
						CharacterName: characterName,
					},
				},
			},
		},
	}
}

func generateMQ(name string, op string, table string, afterId int) models.MQ {
	return models.MQ{
		Schema: nil,
		Payload: models.Payload{
			Before: nil,
			After: models.After{
				Id:          afterId,
				Name:        name,
				CharacterId: 0,
			},
			Source: models.Source{
				Version:   "",
				Connector: "",
				Name:      "",
				TsMs:      0,
				Snapshot:  "",
				Db:        "",
				Sequence:  "",
				Schema:    "",
				Table:     table,
				TxId:      0,
				Lsn:       0,
				Xmin:      nil,
			},
			Op:          op,
			TsMs:        0,
			Transaction: nil},
	}
}

func generateDocumentData(action string, existingId string, actorName string, actorId int, characterName string, characterId int) models.DocData {
	return models.DocData{
		Action:     action,
		ExistingId: existingId,
		Doc: models.Document{
			ActorId:       actorId,
			ActorName:     actorName,
			CharacterId:   characterId,
			CharacterName: characterName,
		},
	}
}
