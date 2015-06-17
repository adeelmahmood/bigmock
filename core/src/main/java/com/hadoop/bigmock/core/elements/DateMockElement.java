package com.hadoop.bigmock.core.elements;

public class DateMockElement extends MockElement {

	private String format;

	public DateMockElement(String name, String format) {
		super(name, ElementType.DATE);
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
