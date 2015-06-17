package com.hadoop.bigmock.core.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.exceptions.ProcessingException;
import com.hadoop.bigmock.core.exceptions.WriterException;
import com.hadoop.bigmock.core.processors.ElementProcessor;
import com.hadoop.bigmock.core.processors.TaskProcessor.MockTaskDefinition;
import com.hadoop.bigmock.core.tasks.LocalMockTask;

@Service
@Scope("prototype")
public class LocalMockDataWriter implements MockDataWriter<LocalMockTask> {

	private static final Logger log = LoggerFactory.getLogger(LocalMockDataWriter.class);

	private final ElementProcessor<MockElement> processor;

	private LocalMockTask task;

	private FileWriter fw;
	private BufferedWriter bw;

	private final static String EOL = "\n";

	private final List<String> columns = new ArrayList<String>();

	@Autowired
	public LocalMockDataWriter(ElementProcessor<MockElement> processor) {
		this.processor = processor;
	}

	@Override
	public void init(MockTaskDefinition<LocalMockTask> definition) throws WriterException {
		task = definition.getTask();
		String folder = task.getFolder();
		String filename = System.nanoTime() + "." + task.getFilename();

		// partition folder
		String partition = "Part=" + definition.getStart() + "-" + definition.getEnd();
		MockElement partitionElement = task.getPartitionElement();
		if (partitionElement != null) {
			try {
				processor.process(partitionElement);
				partition = partitionElement.getName() + "=" + partitionElement.getValue();
			} catch (ProcessingException e) {
				// use auto created partition
			}
		}

		String path = FilenameUtils.normalize(folder + "/" + partition);
		// create folders
		new File(FilenameUtils.normalize(path)).mkdirs();

		try {
			fw = new FileWriter(new File(path, filename));
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			log.error("error in initializing writer for task " + task.getName() + " => " + e.getMessage());
			throw new WriterException("error in initializing writer for task " + task.getName() + " => "
					+ e.getMessage());
		}
	}

	@Override
	public void setHeaders(String[] headers) {
		try {
			String line = "";
			String sep = task.getSeparator();
			// write all headers
			for (String header : headers) {
				line += header + sep;
			}
			// write header line
			bw.write(line.substring(0, line.length() - sep.length()) + EOL);
		} catch (IOException e) {
			log.warn("unable to add headers => " + e.getMessage(), e);
		}
	}

	@Override
	public void rowCompleted(long row) throws WriterException {
		try {
			String line = "";
			String sep = task.getSeparator();

			// write all columns to the line
			for (String value : columns) {
				line += value + sep;
			}

			// write line
			bw.write(line.substring(0, line.length() - sep.length()) + EOL);
		} catch (IOException e) {
			log.error("error in endRow for task " + task.getName() + " => " + e.getMessage(), e);
			throw new WriterException("error in endRow for task " + task.getName() + " => " + e.getMessage(), e);
		}
		columns.clear();
	}

	@Override
	public void flush() throws WriterException {
		try {
			bw.flush();
			bw.close();
		} catch (IOException e) {
			log.error("error in flush for task " + task.getName() + " => " + e.getMessage(), e);
			throw new WriterException("error in flush for task " + task.getName() + " => " + e.getMessage(), e);
		}
	}

	@Override
	public void addColumn(String value) {
		columns.add(value);
	}

}
