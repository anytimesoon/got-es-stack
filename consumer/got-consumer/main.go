package main

import (
	"flag"
	"log"
	"os"
	"time"
)

var (
	uri               = flag.String("uri", "amqp://guest:guest@localhost:5672/", "AMQP URI")
	exchange          = flag.String("exchange", "got_topic", "Durable, non-auto-deleted AMQP exchange name")
	exchangeType      = flag.String("exchange-type", "topic", "Exchange type - direct|fanout|topic|x-custom")
	queue             = flag.String("queue", "got_queue", "Ephemeral AMQP queue name")
	bindingKey        = flag.String("key", "got", "AMQP binding key")
	consumerTag       = flag.String("consumer-tag", "go-consumer", "AMQP consumer tag (should not be blank)")
	lifetime          = flag.Duration("lifetime", 0*time.Second, "lifetime of process before shutdown (0s=infinite)")
	verbose           = flag.Bool("verbose", true, "enable verbose output of message data")
	autoAck           = flag.Bool("auto_ack", false, "enable message auto-ack")
	esIndex           = flag.String("es-index", "got", "Elasticsearch index name")
	esUri             = flag.String("es-uri", "http://localhost:9200", "Elasticsearch URI")
	ErrLog            = log.New(os.Stderr, "[ERROR] ", log.LstdFlags|log.Lmsgprefix)
	Log               = log.New(os.Stdout, "[INFO] ", log.LstdFlags|log.Lmsgprefix)
	deliveryCount int = 0
)

func main() {
	flag.Parse()

	newPersister()

	c, err := newConsumer(*uri, *exchange, *exchangeType, *queue, *bindingKey, *consumerTag)
	if err != nil {
		ErrLog.Fatalf("%s", err)
	}

	setupCloseHandler(c)

	if *lifetime > 0 {
		Log.Printf("running for %s", *lifetime)
		time.Sleep(*lifetime)
	} else {
		Log.Printf("running until consumer is done")
		<-c.done
	}

	Log.Printf("shutting down")

	if err := c.shutdown(); err != nil {
		ErrLog.Fatalf("error during shutdown: %s", err)
	}
}
