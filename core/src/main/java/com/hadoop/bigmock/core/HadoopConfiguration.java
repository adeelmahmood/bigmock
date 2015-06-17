package com.hadoop.bigmock.core;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hive.hcatalog.api.HCatClient;
import org.apache.hive.hcatalog.common.HCatException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ConditionalOnProperty("hadoop")
@Configuration
@ImportResource("classpath:hadoop.xml")
public class HadoopConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public HBaseAdmin hbaseAdmin() throws BeansException, IOException {
		HBaseAdmin admin = new HBaseAdmin(
				(org.apache.hadoop.conf.Configuration) applicationContext.getBean("hbaseConfiguration"));
		return admin;
	}

	@Bean(destroyMethod = "close")
	public HCatClient hCatClient() throws HCatException {
		return HCatClient.create((org.apache.hadoop.conf.Configuration) applicationContext.getBean("hdpConfiguration"));
	}
}
