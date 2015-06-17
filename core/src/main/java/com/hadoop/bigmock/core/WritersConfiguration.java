package com.hadoop.bigmock.core;

import java.util.Properties;

import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hadoop.bigmock.core.writers.WriterLocator;

@Configuration
public class WritersConfiguration {

	@Bean
	public ServiceLocatorFactoryBean writerFactory() {
		ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
		factory.setServiceLocatorInterface(WriterLocator.class);
		// define writer mappings
		Properties props = new Properties();
		props.put("LOCAL", "localMockDataWriter");
		props.put("HIVE", "hiveMockDataWriter");
		factory.setServiceMappings(props);
		return factory;
	}

}
