package com.hadoop.bigmock.core.writers;

import com.hadoop.bigmock.core.tasks.MockTask;

public interface WriterLocator<T extends MockTask> {

	MockDataWriter<T> getWriter(String id);
}
