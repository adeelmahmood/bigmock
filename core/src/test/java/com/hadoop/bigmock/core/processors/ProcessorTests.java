package com.hadoop.bigmock.core.processors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hadoop.bigmock.core.TestConfig;
import com.hadoop.bigmock.core.elements.CountryMockElement;
import com.hadoop.bigmock.core.elements.DateMockElement;
import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.tasks.LocalMockTask;
import com.hadoop.bigmock.core.tasks.MockTask.TargetPlatform;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestConfig.class })
public class ProcessorTests {

	@Autowired
	TaskProcessor<LocalMockTask> processor;

	@Test
	public void test() throws InterruptedException {

		// create a local mock data task
		LocalMockTask task = new LocalMockTask();
		task.setName("local test task");
		task.setMockRecords(77);
		task.setPlatform(TargetPlatform.LOCAL);
		task.setSeparator(",");
		task.setFolder(".");
		task.setFilename("test.txt");

		task.setPartitionElement(new CountryMockElement("country", true));

		// add mock element to task
		task.getElements().add(new MockElement("firstname", ElementType.FIRSTNAME));
		task.getElements().add(new MockElement("lastname", ElementType.LASTNAME));
		task.getElements().add(new MockElement("fullname", ElementType.FULLNAME));
		task.getElements().add(new MockElement("email", ElementType.EMAIL));
		task.getElements().add(new CountryMockElement("country-short", true));
		task.getElements().add(new CountryMockElement("country-long", false));
		task.getElements().add(new MockElement("phone", ElementType.PHONE));
		task.getElements().add(new DateMockElement("born", "YYYY-MM-dd"));
		task.getElements().add(new MockElement("id", ElementType.UUID));

		processor.process(task);

		Thread.sleep(5000);
	}
}
