package com.hadoop.bigmock.core.processors;

import com.hadoop.bigmock.core.exceptions.ProcessingException;

public interface IProcessor<T> {

	T process(T input) throws ProcessingException;
}
