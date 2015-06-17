package com.hadoop.bigmock.core.handlers;

import com.hadoop.bigmock.core.processors.TaskProcessor.MockTaskDefinition;
import com.hadoop.bigmock.core.tasks.MockTask;

public interface TaskHandler<T extends MockTask> {

	void handleTask(MockTaskDefinition<T> definition);
}
