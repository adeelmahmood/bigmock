package com.hadoop.bigmock.core.parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.hadoop.bigmock.core.elements.CountryMockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

@Component
public class CountryParser extends AbstractParser<CountryMockElement> {

	private final String dictionary = "dicts/countries/countries.csv";

	private final List<String> lines;

	public CountryParser() throws IOException {
		lines = Files.readAllLines(Paths.get(new ClassPathResource(dictionary).getFile().getPath()));
	}

	@Override
	public String parse(CountryMockElement element) throws ElementParserException {
		String line = lines.get(rand(0, lines.size()));
		return element.isShortened() ? line.split("\\|")[0] : line.split("\\|")[1];
	}

	@Override
	public ElementType getType() {
		return ElementType.COUNTRY;
	}

}
