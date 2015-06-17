package com.hadoop.bigmock.core.parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

@Component
public class EmailParser extends AbstractParser<MockElement> {

	private final String dictionary = "dicts/emails/emails.csv";

	private final FirstNameParser firstParser;
	private final LastNameParser lastParser;

	private final List<String> lines;

	@Autowired
	public EmailParser(FirstNameParser firstParser, LastNameParser lastParser) throws IOException {
		lines = Files.readAllLines(Paths.get(new ClassPathResource(dictionary).getFile().getPath()));
		this.firstParser = firstParser;
		this.lastParser = lastParser;
	}

	@Override
	public String parse(MockElement element) throws ElementParserException {
		return firstParser.parse(element) + "." + lastParser.parse(element) + "@" + lines.get(rand(0, lines.size()));
	}

	@Override
	public ElementType getType() {
		return ElementType.EMAIL;
	}

}
