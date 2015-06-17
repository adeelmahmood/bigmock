package com.hadoop.bigmock.core.parsers;

import java.util.Random;

import com.hadoop.bigmock.core.elements.MockElement;

public abstract class AbstractParser<T extends MockElement> implements ElementParser<T> {

	protected final Random random = new Random();

	protected int rand(int min, int max) {
		return random.nextInt(max - min) + min;
	}
}
