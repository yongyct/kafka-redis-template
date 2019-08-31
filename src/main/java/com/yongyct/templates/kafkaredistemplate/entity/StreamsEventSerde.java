package com.yongyct.templates.kafkaredistemplate.entity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Custom serde to process JSON from Kafka Topics
 * 
 * @author tommy.yong
 *
 */
public class StreamsEventSerde implements Serializer<StreamsEvent>, Deserializer<StreamsEvent>, Serde<StreamsEvent> {

	private static ObjectMapper mapper = new ObjectMapper();

	private static final byte[] CR = "\r".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] LF = "\n".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] CR_LF = "\r\n".getBytes(StandardCharsets.US_ASCII);

	@Override
	public Serializer<StreamsEvent> serializer() {
		return this;
	}

	@Override
	public Deserializer<StreamsEvent> deserializer() {
		return this;
	}

	@Override
	public StreamsEvent deserialize(String topic, byte[] data) {
		if (data == null || data.length == 0 || data.equals(CR) || data.equals(LF) || data.equals(CR_LF)) {
			throw new IllegalStateException("Invalid Data: [" + data + "]");
		} else {

			JsonNode deserializedData;

			try {
				deserializedData = mapper.readTree(data);
			} catch (IOException e) {
				throw new IllegalStateException(
						"Cannot parse data: [" + new String(data, StandardCharsets.UTF_8) + "] to JSON", e);
			}

			if (deserializedData == null) {
				throw new IllegalStateException("Invalid Data: [" + data + "]");
			} else if (!deserializedData.isArray() && !deserializedData.isObject()) {
				throw new IllegalStateException("Invalid Data: [" + data + "]");
			}

			return new StreamsEvent((ObjectNode) deserializedData);
		}
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public byte[] serialize(String topic, StreamsEvent data) {
		if (data == null) {
			return new byte[0];
		} else {
			try {
				return mapper.writeValueAsBytes(data.getNode());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public void close() {
	}

}
