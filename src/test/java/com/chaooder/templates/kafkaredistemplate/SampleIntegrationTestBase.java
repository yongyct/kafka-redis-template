package com.chaooder.templates.kafkaredistemplate;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.chaooder.templates.kafkaredistemplate.config.KafkaStreamsConfiguration;
import com.chaooder.templates.kafkaredistemplate.config.RedisConfiguration;
import com.chaooder.templates.kafkaredistemplate.config.RedisSession;
import com.chaooder.templates.kafkaredistemplate.entity.StreamsEvent;
import com.chaooder.templates.kafkaredistemplate.entity.StreamsEventSerde;
import com.chaooder.templates.kafkaredistemplate.topology.SampleTopologyConstructor;
import com.chaooder.templates.kafkaredistemplate.topology.TopologyMetadata;

import lombok.extern.slf4j.Slf4j;

@ContextConfiguration(classes = { SampleTopologyConstructor.class, KafkaStreamsConfiguration.class, RedisSession.class,
		RedisConfiguration.class })
@Slf4j
public class SampleIntegrationTestBase extends StreamsIntegrationTestBase {

	@Autowired
	private SampleTopologyConstructor sampleTopologyConstructor;
	@Autowired
	private KafkaStreamsConfiguration config;

	/**
	 * These are kafka streams test utils available for use to inject records
	 * through the topology at test above {@link SampleTopologyConstructor}.
	 * 
	 * {@link TopologyTestDriver} is a pseudo {@link KafkaStreams} that is
	 * instantiated with a topology + streams props.
	 * 
	 * {@link ConsumerRecordFactory} is a factory class to provide a pseudo
	 * {@link ConsumerRecord} to be injected through the topology
	 * 
	 */
	protected TopologyTestDriver driver;
	protected ConsumerRecordFactory<String, StreamsEvent> consumerRecordFactory;

	@Before
	public void setUp() {
		StreamsBuilder builder = new StreamsBuilder();
		TopologyMetadata metadata = new TopologyMetadata();
		sampleTopologyConstructor.constructTopology(builder, metadata);
		Topology topology = builder.build();

		driver = new TopologyTestDriver(topology, config.getKafkaStreamsProperties());
		consumerRecordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new StreamsEventSerde());
	}

	@After
	public void cleanUp() {
		if (driver != null) {
			try {
				driver.close();
			} catch (Exception e) {
				log.error("Test driver cleanup error", e);
			}
		}
	}

}
