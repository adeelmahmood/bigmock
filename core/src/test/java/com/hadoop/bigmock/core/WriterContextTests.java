package com.hadoop.bigmock.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hadoop.bigmock.core.tasks.MockTask;
import com.hadoop.bigmock.core.writers.WriterLocator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestConfig.class })
public class WriterContextTests {

	@Autowired
	WriterLocator<MockTask> locator;

	@Test
	public void test() {
		Assert.assertNotEquals(locator.getWriter("LOCAL"), locator.getWriter("LOCAL"));
	}

}
