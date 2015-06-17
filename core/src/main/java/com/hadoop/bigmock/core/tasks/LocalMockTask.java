package com.hadoop.bigmock.core.tasks;

import com.hadoop.bigmock.core.elements.MockElement;

public class LocalMockTask extends MockTask {

	private String separator;
	private String folder;
	private String filename;

	private MockElement partitionElement;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public MockElement getPartitionElement() {
		return partitionElement;
	}

	public void setPartitionElement(MockElement partitionElement) {
		this.partitionElement = partitionElement;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

}
