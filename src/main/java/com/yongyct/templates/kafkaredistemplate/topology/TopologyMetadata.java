package com.yongyct.templates.kafkaredistemplate.topology;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.TopologyDescription.Node;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.internals.AbstractStream;

/**
 * Class works with 'name' {@link Field} object in {@link AbstractStream} class
 * in Kafka Streams, and uses 'get' method in the above {@link Field} object to
 * get the name of kstream/table with which the 'describe' method is applied to.
 * Thereafter, it tags the kstream/table with its 'name' & 'description'
 * 
 * To add more describe methods if using new implementations of AbstractStream
 * 
 * 'Node' below refers to 'stream processors, sink, or source topics'
 * 
 * @author tommy.yong
 *
 */
public class TopologyMetadata {

	/**
	 * Maps kstream/table name to description thereby getting a node description
	 */
	private Map<String, String> nodeDescription = new HashMap<>();
	private static final String NAME_FIELD = "name";
	private static final Field ABSTRACT_STREAM_NAME_FIELD;

	/**
	 * Initialize ABSTRACT_STREAM_NAME_FIELD
	 */
	static {
		try {
			ABSTRACT_STREAM_NAME_FIELD = AbstractStream.class.getDeclaredField(NAME_FIELD);
		} catch (NoSuchFieldException e) {
			throw new LinkageError("Code depends on field 'name' in AbstractStream, "
					+ "if newer version of kafka is being used, revisit this code", e);
		}
		ABSTRACT_STREAM_NAME_FIELD.setAccessible(true);
	}

	public <K, V> KStream<K, V> describe(KStream<K, V> stream, String description) {
		setNodeDescription((AbstractStream<?>) stream, description);
		return stream;
	}

	public <K, V> KTable<K, V> describe(KTable<K, V> table, String description) {
		setNodeDescription((AbstractStream<?>) table, description);
		return table;
	}

	public String getNodeDescription(Node node) {
		String nodeName = node.name();
		return nodeDescription.get(nodeName);
	}

	/**
	 * Use the {@link getNodeName} method to extract node name from the
	 * kstream/table object being passed to the {@link describe} method, and tags a
	 * given {@code description} to it
	 * 
	 * @param stream
	 * @param description
	 */
	private void setNodeDescription(AbstractStream<?> stream, String description) {
		nodeDescription.put(getNodeName(stream), description);
	}

	/**
	 * Get the node name based on the kstream/table variable being passed into the
	 * {@link describe} method, used by {@link setNodeDescription} method
	 * 
	 * @param stream
	 * @return node name
	 */
	private String getNodeName(AbstractStream<?> stream) {
		String nodeName;
		try {
			nodeName = ABSTRACT_STREAM_NAME_FIELD.get(stream).toString();
		} catch (IllegalAccessException e) {
			throw new LinkageError("Code depends on field 'name' in AbstractStream, "
					+ "if newer version of kafka is being used, revisit this code", e);
		}
		return nodeName;
	}

}
