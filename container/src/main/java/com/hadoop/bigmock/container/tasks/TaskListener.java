package com.hadoop.bigmock.container.tasks;

import com.hadoop.bigmock.container.exceptions.TaskCreateException;
import com.hadoop.bigmock.core.tasks.MockTask;

public interface TaskListener<T extends MockTask> {

	void register() throws Exception;

	void newTask(T task) throws TaskCreateException;
}
