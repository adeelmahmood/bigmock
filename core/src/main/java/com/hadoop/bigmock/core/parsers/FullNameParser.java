package com.hadoop.bigmock.core.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

@Component
public class FullNameParser extends AbstractParser<MockElement> {

	private final FirstNameParser firstParser;
	private final LastNameParser lastParser;

	@Autowired
	public FullNameParser(FirstNameParser firstParser, LastNameParser lastParser) {
		this.firstParser = firstParser;
		this.lastParser = lastParser;
	}

	@Override
	public String parse(MockElement element) throws ElementParserException {
		return firstParser.parse(element) + " " + lastParser.parse(element);
	}

	@Override
	public ElementType getType() {
		return ElementType.FULLNAME;
	}

}
