package com.hadoop.bigmock.core.exceptions;

public class ElementParserException extends Exception {

	private static final long serialVersionUID = 3032006713621789290L;

	public ElementParserException(String msg) {
		super(msg);
	}

	public ElementParserException(String msg, Throwable t) {
		super(msg, t);
	}
}
