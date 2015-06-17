package com.hadoop.bigmock.core.parsers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.hadoop.bigmock.core.elements.DateMockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

@Component
public class DateParser implements ElementParser<DateMockElement> {

	@Override
	public String parse(DateMockElement element) throws ElementParserException {
		DateTime dt = getRandomDate();
		return DateTimeFormat.forPattern(element.getFormat()).print(dt);
	}

	@Override
	public ElementType getType() {
		return ElementType.DATE;
	}

	private DateTime getRandomDate() {
		long offset = DateTime.now().minusYears(50).getMillis();
		long end = DateTime.now().getMillis();
		long diff = end - offset + 1;
		return new DateTime(offset + (long) (Math.random() * diff));
	}
}
