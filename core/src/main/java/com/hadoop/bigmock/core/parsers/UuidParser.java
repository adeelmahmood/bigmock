package com.hadoop.bigmock.core.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

@Component
public class UuidParser implements ElementParser<MockElement> {

	private final IdGenerator idGenerator;

	@Autowired
	public UuidParser(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Override
	public String parse(MockElement element) throws ElementParserException {
		return idGenerator.generateId().toString();
	}

	@Override
	public ElementType getType() {
		return ElementType.UUID;
	}

}
