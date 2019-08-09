package com.chaooder.templates.kafkaredistemplate.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;

/**
 * Configures security related settings when connecting to a Kafka cluster,
 * possibility of enhancements with conditional usages in overall class and
 * {@link jaasConfiguration}
 * 
 * @author tommy.yong
 *
 */
@ConditionalOnProperty("kafka.security.integration")
public class KafkaSecurityConfiguration {

	// Local Constants used
	private static final String SYS_PROPS_KDC = "java.security.krb5.kdc";
	private static final String SYS_PROPS_REALM = "java.security.krb5.realm";
	private static final String KRB5_LOGIN_MODULE = "com.sun.security.auth.module.Krb5LoginModule";

	/**
	 * Kafka Client Configurations, for use in {@link addKafkaSecurityProps} method
	 * 
	 */
	@Value("${kafka.security.protocol}")
	private String securityProtocol;
	@Value("${kafka.sasl.service.name}")
	private String serviceName;
	@Value("${kafka.ssl.truststore.password}")
	private String truststorePassword;

	/**
	 * JAAS Configurations; for use in {@link jaasConfiguration} method/bean
	 * 
	 */
	@Value("${kafka.sasl.kerberos.kdc}")
	private String kdc;
	@Value("${kafka.sasl.kerberos.realm}")
	private String realm;
	@Value("${kafka.sasl.kerberos.principal}")
	private String principal;

	/**
	 * Security related static resources & files
	 * 
	 */
	@Value("${kafka.sasl.kerberos.keytab.location}")
	private String keytabLocation;
	@Value("${kafka.ssl.truststore.location}")
	private String truststoreLocation;

	/**
	 * Instantiates JAAS Configuration as an in-memory configuration when starting
	 * up the JVM, avoiding the use of a "JAAS.conf" file. See sample
	 * {@linkplain https://docs.confluent.io/current/kafka/authentication_sasl/authentication_sasl_gssapi.html#jaas}
	 * 
	 * @return JAAS Configuration
	 * 
	 */
	@Bean
	@Primary
	@ConditionalOnProperty("kafka.security.integration")
	public InMemoryConfiguration jaasConfiguration() {

		System.setProperty(SYS_PROPS_KDC, kdc);
		System.setProperty(SYS_PROPS_REALM, realm);

		Map<String, Object> options = new HashMap<>();
		options.put("keyTab", keytabLocation);
		options.put("principal", principal);
		options.put("useKeyTab", "true");
		options.put("storeKey", "true");

		AppConfigurationEntry kafkaClientConfig = new AppConfigurationEntry(KRB5_LOGIN_MODULE,
				LoginModuleControlFlag.REQUIRED, options);
		Map<String, AppConfigurationEntry[]> jaasConfigEntries = new HashMap<>();
		jaasConfigEntries.put("KafkaClient", new AppConfigurationEntry[] { kafkaClientConfig });

		InMemoryConfiguration jaasConfig = new InMemoryConfiguration(jaasConfigEntries);
		Configuration.setConfiguration(jaasConfig);
		return jaasConfig;

	}

	/**
	 * Null bean that is instantiated in the case where
	 * {@code kafka.security.integration=false}
	 * 
	 * @return Null JAAS Configuration
	 * 
	 */
	@Bean("jaasConfiguration")
	public InMemoryConfiguration nullJaasConfiguration() {
		return null;
	}

	/**
	 * Adds security related properties into kafka streams properties
	 * 
	 * @param props
	 */
	void addKafkaSecurityProperties(Properties props) {
		props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, securityProtocol);
		props.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, serviceName);
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
	}

}
