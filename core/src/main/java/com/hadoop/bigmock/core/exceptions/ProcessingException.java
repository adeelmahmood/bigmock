package com.hadoop.bigmock.core.exceptions;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = -6951804441158250500L;

	public ProcessingException(String msg) {
		super(msg);
	}

	public ProcessingException(String msg, Throwable t) {
		super(msg, t);
	}

}
