package com.hadoop.bigmock.yarn.utils;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigurationLoader {

	public static Properties loadProperties(String filename) throws ConfigurationException {
		Configuration config = new PropertiesConfiguration(filename);
		Properties props = new Properties();
		Iterator<String> keys = config.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			props.put(key, config.getProperty(key));
		}
		return props;
	}
}
