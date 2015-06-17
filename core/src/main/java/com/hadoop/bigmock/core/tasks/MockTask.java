package com.hadoop.bigmock.core.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hadoop.bigmock.core.elements.MockElement;

public abstract class MockTask {

	private String name;
	private long mockRecords;

	private TargetPlatform platform;

	private Date created;
	
	private List<MockElement> elements = new ArrayList<MockElement>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TargetPlatform getPlatform() {
		return platform;
	}

	public void setPlatform(TargetPlatform platform) {
		this.platform = platform;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getMockRecords() {
		return mockRecords;
	}

	public void setMockRecords(long mockRecords) {
		this.mockRecords = mockRecords;
	}

	public List<MockElement> getElements() {
		return elements;
	}

	public void setElements(List<MockElement> elements) {
		this.elements = elements;
	}

	public enum TargetPlatform {
		LOCAL, HIVE, HBASE;
	}

}
