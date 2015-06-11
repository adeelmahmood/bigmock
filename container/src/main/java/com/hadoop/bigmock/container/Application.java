package com.hadoop.bigmock.container;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadoop.bigmock.container.utils.Constants;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Value("${zk.host}")
	private String zkHost;

	@Bean(destroyMethod = "close")
	public CuratorFramework curatorFramework() {
		CuratorFramework curatorFramework = null;
		try {
			// create curator client
			curatorFramework = CuratorFrameworkFactory.builder().connectString(zkHost)
					.retryPolicy(new ExponentialBackoffRetry(1000, 3)).namespace(Constants.ZK_NS_PATH).build();
			// start the client
			curatorFramework.start();
		} catch (Exception e) {
			throw new RuntimeException("error in creating curator client", e);
		}
		return curatorFramework;
	}

	@Bean
	@Primary
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private Worker worker;

	@Override
	public void run(String... args) throws Exception {
		worker.register();
		Thread.sleep(5000);
	}
}
