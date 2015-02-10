# sms-works

Provides a bridge from a rabbit MQ queue to an SMS provider, to send SMS messages.

## Configuration

Configure Twilio and Rabbit-MQ, either with a config.edn or the following environment variables:

    TWILIO_SID = Twilio account SID
    TWILIO_TOKEN = Twilio account auth token
    TWILIO_NUMBERS = Comma separated list of outbound phone numbers
    QUEUE_PORT_5672_TCP_ADDR = Address of Rabbit-MQ host server
    QUEUE_PORT_5672_TCP_PORT = Port of Rabbit-MQ server
    
When running with docker-compose, the QUEUE environment variables are automatically set by the linked container.

## Usage

Send an edn formatted message to the "sms" queue with the following shape:

    {:to "+15556781234"
     :body "This is my SMS message"
     :from "+15551239876" #optional
    }
     
If the `:from` parameter is left off, it will select a random number from the `TWILIO_NUMBERS` configuration.

## Running

### With docker-compose

Build it
`docker-compose build`

Run it
`TWILIO_SID=XXX TWILIO_TOKEN=YYY TWILIO_NUMBERS="[\"+15555555555\" \"+15556789876\"]" docker-compose up`

Substitute in your Twilio SID, Auth Token, and one or more numbers.

Run `docker ps` and look for the first port of the rabbitmq container, that's the port the rabbitmq management console is running on. Open up your browser to localdocker:PORT (assuming you use localdocker to point to the docker VM), and enter using username: 'guest' password: 'guest'.

Click on queues, select 'sms', scroll down and open up `Publish message`. Put an edn formatted message (see Usage for an example) into the Payload and click on the Publish message button. If your credentials and twilio numbers are valid, you should receive an SMS.

## License

Copyright © 2015 Democracy Works, Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
