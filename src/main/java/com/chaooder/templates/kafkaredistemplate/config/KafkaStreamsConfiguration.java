package com.chaooder.templates.kafkaredistemplate.config;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.chaooder.templates.kafkaredistemplate.entity.StreamsEventSerde;

/**
 * Defines kafka streams client properties
 * 
 * @author tommy.yong
 *
 */
@Configuration
public class KafkaStreamsConfiguration extends KafkaSecurityConfiguration {

	/**
	 * Kafka client configuration values
	 */
	@Value("${kafka.streams.application.id}")
	private String appId;
	@Value("${kafka.bootstrap.servers}")
	private String bootstrapServers;
	@Value("${kafka.producer.retries}")
	private String retries;
	@Value("${kafka.producer.retry.backoff.ms}")
	private String retryBackoffMs;
	@Value("${kafka.streams.threads.num}")
	private String numThreads;
	@Value("${kafka.security.integration}")
	private boolean securityIntegration;

	/**
	 * Default serdes & exception handlers for streams
	 */
	private String defaultStreamsKeySerde = StringSerde.class.getName();
	private String defaultStreamsValueSerde = StreamsEventSerde.class.getName();
	private String defaultDeserializationExceptionHandler = LogAndContinueExceptionHandler.class.getName();

	/**
	 * Properties to be used StreamsBuilder during builder.build
	 * 
	 * @return kafka streams properties
	 */
	public Properties getKafkaStreamsProperties() {
		Properties props = new Properties();
		addKafkaStreamsProperties(props);
		if (securityIntegration) {
			addKafkaSecurityProperties(props);
		}
		return props;
	}

	/**
	 * Add kafka streams client related properties
	 * 
	 * @param props
	 */
	private void addKafkaStreamsProperties(Properties props) {
		props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, appId);
		props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, defaultStreamsKeySerde);
		props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, defaultStreamsValueSerde);
		props.setProperty(StreamsConfig.RETRIES_CONFIG, retries);
		props.setProperty(StreamsConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
		props.setProperty(StreamsConfig.NUM_STREAM_THREADS_CONFIG, numThreads);
		props.setProperty(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
				defaultDeserializationExceptionHandler);

	}

}
