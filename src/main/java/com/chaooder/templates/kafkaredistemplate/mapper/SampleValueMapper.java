package com.chaooder.templates.kafkaredistemplate.mapper;

import org.apache.kafka.streams.kstream.ValueMapper;

import com.chaooder.templates.kafkaredistemplate.config.CommonConstants;
import com.chaooder.templates.kafkaredistemplate.config.RedisSession;
import com.chaooder.templates.kafkaredistemplate.entity.StreamsEvent;

import lombok.AllArgsConstructor;

/**
 * Sample {@link ValueMapper} implementation with types <Input Value Type,
 * Output Value Type>.
 * 
 * @author tommy.yong
 *
 */
@AllArgsConstructor
public class SampleValueMapper implements ValueMapper<StreamsEvent, StreamsEvent> {

	private RedisSession redis;

	@Override
	public StreamsEvent apply(StreamsEvent value) {
		value.put(CommonConstants.SAMPLE_EVENT_FIELD, redis.get(CommonConstants.SAMPLE_REDIS_KEY));
		return value;
	}

}
