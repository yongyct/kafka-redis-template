# Sample Kafka & Redis Application
Sample template for building real-time processing applications with Kafka & Redis

## Getting Started

### Prerequisites
You will need running Kafka & Redis instances. To set them up, see:
* Kafka Quickstart: https://kafka.apache.org/quickstart
* Redis Quickstart: https://redis.io/topics/quickstart

### Using the Template
* Main "high-level" dataflow should be scripted into a class implementing `TopologyConstructor` interface under `constructTopology` method (see `SampleTopologyConstructor`)
* Implement your data transformation logic from a variety of transformation interfaces provided by Kafka Streams (e.g. `ValueMapper`, `Transformer`, etc), which will be used by the `constructTopology` method

### Writing Test Cases
* To pipe in test data through Topology/Tranformations, extend the `StreamsIntegrationTestBase` class (see example `SampleIntegrationTestBase`)

### Building the Application
* To build the application, cd to project home and execute `mvn clean install`

### Notes
* `kafka.security.integration` in application.properties - to set to true for integration with [Kerberos](https://web.mit.edu/kerberos/)
* `redis.iscluster` in application.properties - to use redis cluster mode
* Alternative: Use of `@Profile("myprofile")` to instantiate conditional beans (e.g. `KafkaSecurityConfiguration`)
* Alternative: Use of `havingValue = ` in `@ConditionalOnProperty` annotation to turn on/off features
* For more information, see: https://kafka.apache.org/documentation/streams/

## Built With
* [Kafka](https://kafka.apache.org/) - Distributed Streaming Platform for Realtime Data Processing
* [Redis](https://redis.io/) - In-Memory NoSQL KV Database
* [Spring Boot](https://spring.io/projects/spring-boot) - Opinionated Framework for Spring Based Applications for Dependency Injections
* [Java](https://adoptopenjdk.net/) - OpenJDK from AdoptOpenJDK
* [Maven](https://maven.apache.org/) - Dependency Management
* [Lombok](https://projectlombok.org/download) - Framework for Reducing Boilerplate Codes
