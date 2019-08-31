package com.yongyct.templates.kafkaredistemplate.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Configuration class for jasypt encryptor bean, which is used to encrypt
 * passwords for various interfacing systems
 * 
 * @author tommy.yong
 *
 */
@Component
public class JasyptConfiguration {

	@Bean(name = "encryptorBean")
	public StringEncryptor stringEncryptor() {

		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig encryptorConfig = new SimpleStringPBEConfig();

		encryptorConfig.setPassword("changeit");
		encryptorConfig.setAlgorithm("PBEWithMD5AndTripleDES");
		encryptorConfig.setKeyObtentionIterations(10);
		encryptorConfig.setPoolSize(1);
		encryptorConfig.setProviderName("SunJCE");
		encryptorConfig.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		encryptorConfig.setStringOutputType("base64");

		encryptor.setConfig(encryptorConfig);
		return encryptor;

	}

}
