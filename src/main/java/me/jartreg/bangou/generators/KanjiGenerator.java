package me.jartreg.bangou.generators;

import me.jartreg.bangou.TextGenerator;

public class KanjiGenerator extends TextGenerator {
	private static final String[] DIGITS = {
			"",
			"",
			"二",
			"三",
			"四",
			"五",
			"六",
			"七",
			"八",
			"九"
	};

	@Override
	public String getZero() {
		return "零";
	}

	@Override
	protected String getDigit(int digit, int zeroes) {
		switch (zeroes) {
			case 0:
				return digit == 1 ? "一" : DIGITS[digit];
			case 1:
				return DIGITS[digit] + "十";
			case 2:
				return DIGITS[digit] + "百";
			case 3:
				return DIGITS[digit] + "千";
		}

		return null;
	}

	@Override
	protected String getPartSuffix(int zeroes) {
		switch (zeroes) {
			case 4:
				return "万";
			case 8:
				return "億";
		}

		return null;
	}
}
