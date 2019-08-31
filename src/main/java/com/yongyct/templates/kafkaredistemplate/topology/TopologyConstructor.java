package com.yongyct.templates.kafkaredistemplate.topology;

import org.apache.kafka.streams.StreamsBuilder;

/**
 * Interface for other Toplogies to extend from for constructing use case specific topologies 
 * 
 * @author tommy.yong
 *
 */
public interface TopologyConstructor {

	public void constructTopology(StreamsBuilder builder, TopologyMetadata metadata);
	
}
