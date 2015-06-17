package com.hadoop.bigmock.core.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;

import com.hadoop.bigmock.core.tasks.MockTask;
import com.hadoop.bigmock.core.utils.Constants;

@Service
public class TaskProcessor<T extends MockTask> implements IProcessor<T> {

	private static final Logger log = LoggerFactory.getLogger(TaskProcessor.class);

	private final EventBus eventBus;

	@Value("${num.workers}")
	private int numWorkers;

	private float partitionPerc = 0.1F;

	@Autowired
	public TaskProcessor(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public T process(T task) {
		log.debug("processing mock task " + task.getName() + " for creating " + task.getMockRecords() + " mock records");

		double batchSize = Math.ceil(task.getMockRecords() / numWorkers);
		log.debug("batch size set to " + batchSize + " for " + numWorkers + " workers");

		int partitionSize = (int) Math.ceil(batchSize * partitionPerc);
		log.debug("partition size set to " + partitionSize);

		long start = 1;
		long end = partitionSize;
		// request work for each partition
		for (int i = 1, j = (int) Math.ceil(batchSize / partitionSize); i <= j; i++) {
			// create execution task with bounds
			MockTaskDefinition<T> def = new MockTaskDefinition<T>(task, start, end);

			// send signal to execute this partition of this task
			eventBus.notify(Constants.MOCK_TASK_SIGNAL, Event.wrap(def));

			// increment bounds
			start = end + 1;
			end += partitionSize;
			if (end > batchSize) {
				end = (long) batchSize;
			}
		}
		return task;
	}

	public static class MockTaskDefinition<T extends MockTask> {

		private final T task;
		private final long start;
		private final long end;

		public MockTaskDefinition(T task, long start, long end) {
			this.task = task;
			this.start = start;
			this.end = end;
		}

		public T getTask() {
			return task;
		}

		public long getStart() {
			return start;
		}

		public long getEnd() {
			return end;
		}

	}
}
