package com.hadoop.bigmock.core;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;

import reactor.Environment;
import reactor.bus.EventBus;
import reactor.core.support.UUIDUtils;
import reactor.spring.context.config.EnableReactor;

@Configuration
@EnableReactor
public class ReactorConfiguration {

	static {
		Environment.initializeIfEmpty().assignErrorJournal();
	}

	@Bean
	public EventBus eventBus() {
		return EventBus.config().env(Environment.get()).dispatcher(Environment.WORK_QUEUE).get();
	}

	@Bean
	public EventBus singleThreadedEventBus() {
		return EventBus.config().env(Environment.get()).synchronousDispatcher().get();
	}

	@Bean
	public IdGenerator randomUUIDGenerator() {
		return new IdGenerator() {
			@Override
			public UUID generateId() {
				return UUIDUtils.random();
			}
		};
	}
}
