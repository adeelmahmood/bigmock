package com.hadoop.bigmock.container.exceptions;

public class TaskCreateException extends Exception {

	private static final long serialVersionUID = 49392386167567142L;

	public TaskCreateException(String msg) {
		super(msg);
	}

	public TaskCreateException(String msg, Throwable t) {
		super(msg, t);
	}
}
