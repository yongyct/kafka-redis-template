package com.yongyct.templates.kafkaredistemplate.topology;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.yongyct.templates.kafkaredistemplate.config.KafkaStreamsConfiguration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Runs through all the topologies implemented via {@link TopologyConstructor},
 * thereby allowing the data processing to flow through. Provides entry point to
 * the whole dataflow topologies with the {@link init} method that is executed
 * with {@link PostConstruct} annotation. The class implements
 * {@link ApplicationContextAware} to ensure that all beans are set in place
 * before the KafkaStreams instance is started, being annotated with
 * {@link EventListener} to check when application/beans are ready
 * 
 * 
 * @author tommy.yong
 *
 */
@Component
@DependsOn("jaasConfiguration")
@Slf4j
public class TopologyRunner implements ApplicationContextAware {

	private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;

	/**
	 * Generic kafka streams properties bean that will be used for a typical streams
	 * app, bean defined in {@link KafkaStreamsConfiguration} class. To autowire
	 * more of such beans configured for specific use cases for each implementation
	 * of {@link ApplicationContextAware} of {@link TopologyRunner}s
	 */
	@Autowired
	private KafkaStreamsConfiguration kafkaStreamsConfiguration;

	/**
	 * Wires all bean within {@link ApplciationContext} implementing
	 * {@link TopologyConstructor} to be called within {@link init} method
	 */
	@Autowired
	private List<TopologyConstructor> constructors;

	/**
	 * {@link KafkaStreams} instance which will take in a {@link Topology} and
	 * {@link kafkaStreamsProperties} to start the whole dataflow. Volatile modifier
	 * to ensure consistency of app state when running across multiple threads
	 */
	@Getter
	private volatile KafkaStreams streams;

	@Setter
	private ApplicationContext applicationContext;

	/**
	 * Entry point for all the wired topologies. {@link StreamsBuilder} &
	 * {@link TopologyMetadata} will be used to wire multiple implementations of
	 * {@link TopologyConstructor} to create a unified {@link Topology}.
	 * {@link Topology} & {@link kafkaStreamsProperties} will then be passed to
	 * instantiate a new {@link KafkaStreams} instance
	 */
	@PostConstruct
	public void init() {
		log.info("Initializing with {} topology constructors: {}", constructors.size(), constructors);
		StreamsBuilder builder = new StreamsBuilder();
		TopologyMetadata metadata = new TopologyMetadata();
		for (TopologyConstructor constructor : constructors) {
			constructor.constructTopology(builder, metadata);
		}
		Topology topology = builder.build();
		streams = new KafkaStreams(topology, kafkaStreamsConfiguration.getKafkaStreamsProperties());
		log.info("Topology: {}", topology.describe());
	}

	/**
	 * Starts the kafka streams app
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void start() {
		streams.setUncaughtExceptionHandler((thread, exception) -> {
			ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) applicationContext;
			ctx.close();
			log.error("Streams Uncaught Exception: ", exception);
		});
		log.info("Starting Kafka Streams App:");
		streams.start();
		log.info("Started in state {}", streams.state());
	}

	/**
	 * Shuts down kafka streams app before terminating jvm
	 */
	@PreDestroy
	public void shutdown() {
		log.info("Shutting down Kafka streams app");
		if (streams != null) {
			boolean success = streams.close(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			log.info("Shutdown {}", success ? "successful"
					: "not yet successful after waiting for " + SHUTDOWN_TIMEOUT_SECONDS + " seconds");
		} else {
			log.info("Kafka Streams App wasn't initialized, shutdown complete");
		}
	}

}
