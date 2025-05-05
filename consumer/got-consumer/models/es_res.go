package models

type EsRes struct {
	Took     int         `json:"took"`
	TimedOut bool        `json:"timed_out"`
	Shards   interface{} `json:"_shards"`
	Hits     Hits        `json:"hits"`
}

type Hits struct {
	Total    Total       `json:"total"`
	MaxScore interface{} `json:"max_score"`
	Hits     []SourceDoc `json:"hits"`
}

type Total struct {
	Value    int    `json:"value"`
	Relation string `json:"relation"`
}

type SourceDoc struct {
	Index  string   `json:"_index"`
	Id     string   `json:"_id"`
	Score  float64  `json:"_score"`
	Source Document `json:"_source"`
}
