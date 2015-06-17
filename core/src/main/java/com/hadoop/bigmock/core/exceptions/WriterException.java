package com.hadoop.bigmock.core.exceptions;

public class WriterException extends Exception {

	private static final long serialVersionUID = 6970325402643158873L;

	public WriterException(String msg) {
		super(msg);
	}

	public WriterException(String msg, Throwable t) {
		super(msg, t);
	}

}
