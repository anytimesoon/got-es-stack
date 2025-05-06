package models

type MQ struct {
	Schema  interface{} `json:"schema"`
	Payload Payload     `json:"payload"`
}

type Payload struct {
	Before      Resource    `json:"before"`
	After       Resource    `json:"after"`
	Source      Source      `json:"source"`
	Op          string      `json:"op"`
	TsMs        int64       `json:"ts_ms"`
	Transaction interface{} `json:"transaction"`
}

type Resource struct {
	Id          int    `json:"id"`
	Name        string `json:"name"`
	CharacterId int    `json:"character_id"`
}

type Source struct {
	Version   string      `json:"version"`
	Connector string      `json:"connector"`
	Name      string      `json:"name"`
	TsMs      int64       `json:"ts_ms"`
	Snapshot  string      `json:"snapshot"`
	Db        string      `json:"db"`
	Sequence  string      `json:"sequence"`
	Schema    string      `json:"schema"`
	Table     string      `json:"table"`
	TxId      int         `json:"txId"`
	Lsn       int         `json:"lsn"`
	Xmin      interface{} `json:"xmin"`
}
