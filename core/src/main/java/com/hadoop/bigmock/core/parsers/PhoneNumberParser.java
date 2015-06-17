package com.hadoop.bigmock.core.parsers;

import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

import com.hadoop.bigmock.core.elements.MockElement;
import com.hadoop.bigmock.core.elements.MockElement.ElementType;
import com.hadoop.bigmock.core.exceptions.ElementParserException;

@Component
public class PhoneNumberParser extends AbstractParser<MockElement> {

	@Override
	public String parse(MockElement element) throws ElementParserException {
		int num1 = (random.nextInt(7) + 1) * 100 + (random.nextInt(8) * 10) + random.nextInt(8);
		int num2 = random.nextInt(743);
		int num3 = random.nextInt(10000);

		DecimalFormat df3 = new DecimalFormat("000"); // 3 zeros
		DecimalFormat df4 = new DecimalFormat("0000"); // 4 zeros

		String phoneNumber = df3.format(num1) + "-" + df3.format(num2) + "-" + df4.format(num3);
		return phoneNumber;
	}

	@Override
	public ElementType getType() {
		return ElementType.PHONE;
	}

}
