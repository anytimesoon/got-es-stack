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
			"http://localhost:9200",
		},
	}
	p, err = elasticsearch.NewClient(cfg)
	if err != nil {
		ErrLog.Fatalf("Error creating the client: %s", err)
	}

	_, err = p.Indices.Create(*esIndex)
	if err != nil {
		ErrLog.Fatalf("Error creating index: %s", err)
	}
}

func persist(msg models.MQ) error {
	existingDoc, err := getExisting(msg)
	if err != nil {
		return err
	}

	data := newDocData(existingDoc, msg)

	d, err := json.Marshal(data.Doc)
	if err != nil {
		ErrLog.Printf("Error marshalling document: %s", err)
		return err
	}

	switch data.Action {
	case "create":
		return persistNewDoc(d)
	case "delete":
		return deleteExistingDoc(data)
	default:
		return updateExistingDoc(data, d)
	}
}

func persistNewDoc(d []byte) error {
	_, err := p.Index(*esIndex, bytes.NewReader(d))
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

func updateExistingDoc(data models.DocData, d []byte) error {
	_, err := p.Update(*esIndex, data.ExistingId, bytes.NewReader(d))
	if err != nil {
		ErrLog.Printf("Error updating document: %s", err)
	}
	Log.Printf("Document updated: %s", data.ExistingId)
	return nil
}

func getExisting(msg models.MQ) (models.EsRes, error) {
	query := fmt.Sprintf(`{
			  "query": { 
				"bool": { 
				  "should": [
					{ "match": { "actor_name": "%s" }},
					{ "match": { "character_name": "%s" }}
				  ]
				}
			  }
			}`, msg.Payload.After.Name, msg.Payload.After.Name)

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
