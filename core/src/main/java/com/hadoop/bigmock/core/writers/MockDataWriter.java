package com.hadoop.bigmock.core.writers;

import com.hadoop.bigmock.core.exceptions.WriterException;
import com.hadoop.bigmock.core.processors.TaskProcessor.MockTaskDefinition;
import com.hadoop.bigmock.core.tasks.MockTask;

public interface MockDataWriter<T extends MockTask> {

	void init(MockTaskDefinition<T> definition) throws WriterException;

	void setHeaders(String[] headers);

	void rowCompleted(long row) throws WriterException;

	void flush() throws WriterException;

	void addColumn(String value);

}
