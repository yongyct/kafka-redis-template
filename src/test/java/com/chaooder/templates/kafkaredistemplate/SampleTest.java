package com.chaooder.templates.kafkaredistemplate;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yongyct.templates.kafkaredistemplate.config.CommonConstants;
import com.yongyct.templates.kafkaredistemplate.config.RedisSession;
import com.yongyct.templates.kafkaredistemplate.entity.StreamsEvent;
import com.yongyct.templates.kafkaredistemplate.entity.StreamsEventSerde;

public class SampleTest extends SampleIntegrationTestBase {

	@MockBean
	private RedisSession redis;
	@Value("${kafka.input.topic}")
	private String inTopic;
	@Value("${kafka.output.topic}")
	private String outTopic;

	private ObjectMapper mapper;

	private String testStreamEvent = "{}";

	@Test
	public void test() throws IOException {

		String expected = "test";
		String actual = "";

		mapper = new ObjectMapper();
		Mockito.when(redis.get(CommonConstants.SAMPLE_REDIS_KEY)).thenReturn(expected);
		ObjectNode testNode = mapper.readValue(testStreamEvent, ObjectNode.class);
		StreamsEvent testEvent = new StreamsEvent(testNode);

		ConsumerRecord<byte[], byte[]> testRecord = consumerRecordFactory.create(inTopic, "key", testEvent);
		driver.pipeInput(testRecord);

		actual = driver.readOutput(outTopic, new StringDeserializer(), new StreamsEventSerde()).value()
				.get(CommonConstants.SAMPLE_EVENT_FIELD).asText();

		System.out.println();
		Assert.assertEquals(expected, actual);
	}

}
