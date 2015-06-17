package com.hadoop.bigmock.core.parsers;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

public interface ElementParser<T extends MockElement> {

	String parse(T element) throws ElementParserException;

	ElementType getType();
}
