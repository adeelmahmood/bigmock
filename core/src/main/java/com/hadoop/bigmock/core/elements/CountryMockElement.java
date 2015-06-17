package com.hadoop.bigmock.core.elements;

public class CountryMockElement extends MockElement {

	private boolean shortened;

	public CountryMockElement(String name, boolean shortened) {
		super(name, ElementType.COUNTRY);
		this.shortened = shortened;
	}

	public boolean isShortened() {
		return shortened;
	}

	public void setShortened(boolean shortened) {
		this.shortened = shortened;
	}

}
