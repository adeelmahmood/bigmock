package com.hadoop.bigmock.core.processors;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.exceptions.ElementParserException;
import com.hadoop.bigmock.core.exceptions.ProcessingException;
import com.hadoop.bigmock.core.parsers.ElementParser;

@Service
public class ElementProcessor<T extends MockElement> implements IProcessor<T> {

	private static final Logger log = LoggerFactory.getLogger(ElementProcessor.class);

	private final List<ElementParser<T>> parsers;

	@Autowired
	public ElementProcessor(List<ElementParser<T>> parsers) {
		this.parsers = parsers;
	}

	@Override
	public T process(T element) throws ProcessingException {
		// get the parser for this element
		ElementParser<T> parser = getParser(element);
		try {
			if (parser == null) {
				throw new ElementParserException("no parser found for element type " + element.getType());
			}
			// parse the element to produce output
			String value = parser.parse(element);
			element.setValue(value);

		} catch (ElementParserException e) {
			log.error("error in parsing element " + element + " => " + e.getMessage(), e);
			throw new ProcessingException("error in parsing element " + element + " => " + e.getMessage(), e);
		}
		return element;
	}

	private ElementParser<T> getParser(T element) {
		for (ElementParser<T> parser : parsers) {
			if (parser.getType() == element.getType()) {
				return parser;
			}
		}
		return null;
	}
}
