package com.hadoop.bigmock.core.parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

@Component
public class FirstNameParser extends AbstractParser<MockElement> {

	private final String dictionary = "dicts/names/first.csv";

	private final List<String> lines;

	public FirstNameParser() throws IOException {
		lines = Files.readAllLines(Paths.get(new ClassPathResource(dictionary).getFile().getPath()));
		System.out.println("first names === " + lines.size());
	}

	@Override
	public String parse(MockElement element) throws ElementParserException {
		return lines.get(rand(0, lines.size()));
	}

	@Override
	public ElementType getType() {
		return ElementType.FIRSTNAME;
	}

}
