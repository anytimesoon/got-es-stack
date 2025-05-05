package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/elastic/go-elasticsearch/v9"
	"github.com/foam-got/consumer/got-consumer/models"
	"strings"
)

var p *elasticsearch.Client

func newPersister() {
	var err error
	cfg := elasticsearch.Config{
		Addresses: []string{
			*esUri,
		},
	}
	p, err = elasticsearch.NewClient(cfg)
	if err != nil {
		ErrLog.Fatalf("Error creating the client: %s", err)
	}

	exists, err := p.Indices.Exists([]string{*esIndex})
	if err != nil {
		ErrLog.Fatalf("Error checking if index exists: %s", err)
	}

	if exists.StatusCode == 404 {
		_, err = p.Indices.Create(*esIndex)
		if err != nil {
			ErrLog.Fatalf("Error creating index: %s", err)
		}
		Log.Printf("Created index: %s", *esIndex)
	} else {
		Log.Printf("Index %s already exists", *esIndex)
	}
}

func persist(msg models.MQ) error {
	existingDoc, err := getExisting(msg)
	if err != nil {
		return err
	}

	data := newDocData(existingDoc, msg)

	switch data.Action {
	case "create":
		return persistNewDoc(data)
	case "delete":
		return deleteExistingDoc(data)
	case "join":
		return joinDocs(data)
	default:
		return updateExistingDoc(data)
	}
}

func joinDocs(data models.DocData) error {
	updateBody := map[string]interface{}{
		"doc": data.Doc,
	}
	d, err := json.Marshal(updateBody)
	if err != nil {
		return err
	}

	res, err := p.Update(*esIndex, data.ExistingId, bytes.NewReader(d))
	if err != nil {
		ErrLog.Printf("Error updating document: %s", err)
	}
	Log.Printf("Document updated: %s", res)
	_, err = p.Delete(*esIndex, data.DeleteId)
	if err != nil {
		ErrLog.Printf("Error deleting document: %s", err)
		return err
	}
	return nil
}

func persistNewDoc(data models.DocData) error {
	d, err := json.Marshal(data.Doc)
	if err != nil {
		ErrLog.Printf("Error marshalling document: %s", err)
		return err
	}

	_, err = p.Index(*esIndex, bytes.NewReader(d))
	if err != nil {
		ErrLog.Printf("Error indexing document: %s", err)
		return err
	}
	Log.Printf("Document indexed: %s", string(d))
	return nil
}

func deleteExistingDoc(data models.DocData) error {
	_, err := p.Delete(*esIndex, data.ExistingId)
	if err != nil {
		ErrLog.Printf("Error deleting document: %s", err)
		return err
	}
	Log.Printf("Document deleted: %s", data.ExistingId)
	return nil
}

func updateExistingDoc(data models.DocData) error {
	updateBody := map[string]interface{}{
		"doc": data.Doc,
	}
	d, err := json.Marshal(updateBody)
	if err != nil {
		return err
	}

	_, err = p.Update(*esIndex, data.ExistingId, bytes.NewReader(d))
	if err != nil {
		ErrLog.Printf("Error updating document: %s", err)
	}
	Log.Printf("Document updated: %s", data.ExistingId)
	return nil
}

func getExisting(msg models.MQ) (models.EsRes, error) {
	query := buildQuery(msg)
	res, err := p.Search(
		p.Search.WithBody(strings.NewReader(query)),
	)
	if err != nil {
		ErrLog.Printf("Error searching: %s", err)
	}

	var result models.EsRes
	if err = json.NewDecoder(res.Body).Decode(&result); err != nil {
		return result, err
	}

	return result, nil
}

func buildQuery(msg models.MQ) string {
	switch msg.Payload.Source.Table {
	case "actor":
		return fmt.Sprintf(`{
			  "query": { 
				"bool": { 
				  "should": [
					{ "match": { "actor_id": %d }},
					{ "match": { "character_id": %d }}
				  ]
				}
			  }
			}`, msg.Payload.After.Id, msg.Payload.After.CharacterId)
	default:
		return fmt.Sprintf(`{
			  "query": { 
				"bool": { 
				  "should": [
					{ "match": { "character_id": "%d" }}
				  ]
				}
			  }
			}`, msg.Payload.After.Id)
	}

}
