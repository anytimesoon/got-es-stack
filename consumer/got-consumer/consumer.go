package main

import (
	"encoding/json"
	"fmt"
	"github.com/foam-got/consumer/got-consumer/models"
	amqp "github.com/rabbitmq/amqp091-go"
	"os"
	"os/signal"
	"syscall"
)

type consumer struct {
	conn    *amqp.Connection
	channel *amqp.Channel
	tag     string
	done    chan error
}

func setupCloseHandler(consumer *consumer) {
	c := make(chan os.Signal, 2)
	signal.Notify(c, os.Interrupt, syscall.SIGTERM)
	go func() {
		<-c
		Log.Printf("Ctrl+C pressed in Terminal")
		if err := consumer.shutdown(); err != nil {
			ErrLog.Fatalf("error during shutdown: %s", err)
		}
		os.Exit(0)
	}()
}

func newConsumer(amqpURI, exchange, exchangeType, queueName, key, ctag string) (*consumer, error) {
	c := &consumer{
		conn:    nil,
		channel: nil,
		tag:     ctag,
		done:    make(chan error),
	}

	var err error

	config := amqp.Config{Properties: amqp.NewConnectionProperties()}
	config.Properties.SetClientConnectionName("sample-consumer")
	Log.Printf("dialing %q", amqpURI)
	c.conn, err = amqp.DialConfig(amqpURI, config)
	if err != nil {
		return nil, fmt.Errorf("Dial: %s", err)
	}

	go func() {
		Log.Printf("closing: %s", <-c.conn.NotifyClose(make(chan *amqp.Error)))
	}()

	Log.Printf("got Connection, getting Channel")
	c.channel, err = c.conn.Channel()
	if err != nil {
		return nil, fmt.Errorf("Channel: %s", err)
	}

	Log.Printf("got Channel, declaring Exchange (%q)", exchange)
	if err = c.channel.ExchangeDeclare(
		exchange,     // name of the exchange
		exchangeType, // type
		true,         // durable
		false,        // delete when complete
		false,        // internal
		false,        // noWait
		nil,          // arguments
	); err != nil {
		return nil, fmt.Errorf("Exchange Declare: %s", err)
	}

	Log.Printf("declared Exchange, declaring Queue %q", queueName)
	queue, err := c.channel.QueueDeclare(
		queueName, // name of the queue
		true,      // durable
		false,     // delete when unused
		false,     // exclusive
		false,     // noWait
		nil,       // arguments
	)
	if err != nil {
		return nil, fmt.Errorf("Queue Declare: %s", err)
	}

	Log.Printf("declared Queue (%q %d messages, %d consumers), binding to Exchange (key %q)",
		queue.Name, queue.Messages, queue.Consumers, key)

	if err = c.channel.QueueBind(
		queue.Name, // name of the queue
		key,        // bindingKey
		exchange,   // sourceExchange
		false,      // noWait
		nil,        // arguments
	); err != nil {
		return nil, fmt.Errorf("Queue Bind: %s", err)
	}

	Log.Printf("Queue bound to Exchange, starting Consume (consumer tag %q)", c.tag)
	deliveries, err := c.channel.Consume(
		queue.Name, // name
		c.tag,      // consumerTag,
		*autoAck,   // autoAck
		false,      // exclusive
		false,      // noLocal
		false,      // noWait
		nil,        // arguments
	)
	if err != nil {
		return nil, fmt.Errorf("Queue Consume: %s", err)
	}

	go handle(deliveries, c.done)

	return c, nil
}

func (c *consumer) shutdown() error {
	// will close() the deliveries channel
	if err := c.channel.Cancel(c.tag, true); err != nil {
		return fmt.Errorf("consumer cancel failed: %s", err)
	}

	if err := c.conn.Close(); err != nil {
		return fmt.Errorf("AMQP connection close error: %s", err)
	}

	defer Log.Printf("AMQP shutdown OK")

	// wait for handle() to exit
	return <-c.done
}

func handle(deliveries <-chan amqp.Delivery, done chan error) {
	cleanup := func() {
		Log.Printf("handle: deliveries channel closed")
		done <- nil
	}

	defer cleanup()

	for d := range deliveries {
		deliveryCount++
		if *verbose == true {
			if string(d.Body) != "default" {
				Log.Printf(
					"got %dB delivery: [%v] %q",
					len(d.Body),
					d.DeliveryTag,
					d.Body,
				)
				msg := models.MQ{}
				err := json.Unmarshal(d.Body, &msg)
				if err != nil {
					ErrLog.Printf("error unmarshalling message: %s", err)
				}
				Log.Printf("message: %+v", msg)
				err = persist(msg)
				if err != nil {
					ErrLog.Printf("error persisting message: %s", err)
					d.Nack(false, true)
				}
			}
		} else {
			if deliveryCount%65536 == 0 {
				Log.Printf("delivery count %d", deliveryCount)
			}
		}
		if *autoAck == false {
			d.Ack(false)
		}
	}
}
