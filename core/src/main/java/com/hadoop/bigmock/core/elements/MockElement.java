package com.hadoop.bigmock.core.elements;

public class MockElement {

	private String name;
	private String value;

	private ElementType type;

	public MockElement() {
	}

	public MockElement(String name, ElementType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public enum ElementType {
		FIRSTNAME, LASTNAME, FULLNAME, EMAIL, COUNTRY, PHONE, DATE, UUID
	}
}
