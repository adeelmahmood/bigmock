package com.hadoop.bigmock.core.handlers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.bus.EventBus;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.exceptions.ProcessingException;
import com.hadoop.bigmock.core.exceptions.WriterException;
import com.hadoop.bigmock.core.processors.ElementProcessor;
import com.hadoop.bigmock.core.processors.TaskProcessor.MockTaskDefinition;
import com.hadoop.bigmock.core.tasks.MockTask;
import com.hadoop.bigmock.core.utils.Constants;
import com.hadoop.bigmock.core.writers.MockDataWriter;
import com.hadoop.bigmock.core.writers.WriterLocator;

@Consumer
public class DefaultTaskHandler<T extends MockTask> extends AbstractHandler<T> {

	private static final Logger log = LoggerFactory.getLogger(DefaultTaskHandler.class);

	private final ElementProcessor<MockElement> processor;
	private final WriterLocator<T> writerLocator;

	@Autowired
	public DefaultTaskHandler(EventBus eventBus, ElementProcessor<MockElement> processor, WriterLocator<T> writerLocator) {
		super(eventBus);
		this.processor = processor;
		this.writerLocator = writerLocator;
	}

	@Selector(Constants.MOCK_TASK_SIGNAL)
	@Override
	public void handleTask(MockTaskDefinition<T> definition) {
		T task = definition.getTask();
		long start = definition.getStart();
		long end = definition.getEnd();
		log.debug("handling mock task " + task.getName() + " for partition " + start + "-" + end);

		// get a hold of writer for this task
		MockDataWriter<T> writer = writerLocator.getWriter(task.getPlatform().toString());

		try {
			writer.init(definition);

			// column names (header)
			List<String> headers = new ArrayList<String>();
			for (MockElement element : task.getElements()) {
				headers.add(element.getName());
			}
			writer.setHeaders(headers.toArray(new String[0]));

			// data
			for (long i = start; i <= end; i++) {
				// for each mock element
				for (MockElement element : task.getElements()) {
					// process the element to generate output value
					try {
						processor.process(element);
					} catch (ProcessingException e) {
						dispatch(Constants.MOCK_TASK_FAILED_SIGNAL, e, task);
						return;
					}

					// add value
					writer.addColumn(element.getValue());
				}

				writer.rowCompleted(i);
			}
			writer.flush();
		} catch (WriterException e) {
			dispatch(Constants.MOCK_TASK_FAILED_SIGNAL, e, task);
		}
	}
}
