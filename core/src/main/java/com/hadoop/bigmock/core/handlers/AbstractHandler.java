package com.hadoop.bigmock.core.handlers;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.tuple.Tuple;

import com.hadoop.bigmock.core.tasks.MockTask;

public abstract class AbstractHandler<T extends MockTask> implements TaskHandler<T> {

	private final EventBus eventBus;

	public AbstractHandler(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	protected void dispatch(String selector, Throwable t, T task) {
		eventBus.notify(selector, Event.wrap(Tuple.of(t, task)));
	}
}
