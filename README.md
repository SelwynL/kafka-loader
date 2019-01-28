# kafka-loader
Load template based records with randomized fields into Kafka

# Run
To run this application
```
sbt clean run
```

# Test
To execute unit tests
```
sbt clean test
```

# Build Artifact
To build a fat JAR which includes all dependencies
```
sbt clean assembly
```

# Test Environment

### SSL

To secure Kafka with SSL you need a keystore and truststore. If you already have these, add them to the appropriate `ssl/keystore` and `ssl/truststore` directories. If not, you can generate them with the `ssl/generate_sample_ssl.sh` script provided:
```
./ssl/generate_sample_ssl.sh
```

### Docker
The local environment is instantiated though `docker-compose`, this requires the install of Docker. See this [StackOverflow](https://stackoverflow.com/a/43365425) post for installing Docker on Mac with Homebrew.

When SSL files are in place, you can create the required services by spinning up the containers in their network. This starts Zookeeper, Kafka, and Schema Registry in the background. For Docker installation see [Docker](#docker-setup) section. Note it may take a minute or two for all the services to come up fully.
```
docker-compose up -d
```

To take down the Docker network and services
```
docker-compose down
```

See the Docker Compose docs, for managing the environment: https://docs.docker.com/compose/reference/

### Kafka
Kafka is running in Docker, but can be interacted with using the regular Kafka CLI tools. These are included when installing [Confluent](https://docs.confluent.io/current/installation/installing_cp/dev-cli.html#installing-cp). This can also be done with Homebrew on Mac:
```
brew install confluent-oss
```

Create a topic called `users`
```
/usr/local/bin/kafka-topics --create --if-not-exists --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic users
```

Note you'll need to install (jq)[https://stedolan.github.io/jq/] to easily escape the file needed for uploading Avro schemas to Schema Registry
```
brew install jq
```

Use `curl` and `jq` to add a schema associated with the `users` topic
```
TOPIC_NAME=users
SCHEMA_FILE_PATH=src/test/resources/user-schema.avsc
curl -X POST -H "Content-Type: application/json" --data "$(jq '{"schema": . | tostring }' $SCHEMA_FILE_PATH)" http://localhost:8081/subjects/$TOPIC_NAME-value/versions
```


# Plugins
- scalafmt: https://scalameta.org/scalafmt/
- wartremover: https://www.wartremover.org/