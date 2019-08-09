package com.chaooder.templates.kafkaredistemplate.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

/**
 * Wraps on top of ObjectNode; functions as a "model" to process JSON records
 * from Kafka topic. Further methods to be added depending on use cases.
 * 
 * @author tommy.yong
 *
 */
@Data
public class StreamsEvent {

	private final ObjectNode node;

	public StreamsEvent(ObjectNode node) {
		if (node == null) {
			throw new NullPointerException("Invalid JSON");
		}
		this.node = node;
	}

	public JsonNode get(String fieldName) {
		/**
		 * Helps prevent first level of NPE by explicitly setting null field
		 */
		if (node.get(fieldName) == null) {
			node.set(fieldName, null);
		}
		return node.get(fieldName);
	}

	// Put/Set Methods
	public void put(String fieldName, String value) {
		node.put(fieldName, value);
	}

	public void put(String fieldName, int value) {
		node.put(fieldName, value);
	}

	public void put(String fieldName, long value) {
		node.put(fieldName, value);
	}

	public void put(String fieldName, boolean value) {
		node.put(fieldName, value);
	}

	public void put(String fieldName, double value) {
		node.put(fieldName, value);
	}

	public void set(String fieldName, JsonNode value) {
		node.set(fieldName, value);
	}
	// End of Put/Set Methods

	public void remove(String fieldName) {
		node.remove(fieldName);
	}

	public ObjectNode with(String fieldName) {
		return node.with(fieldName);
	}

}
