package me.jartreg.bangou;

import java.util.ArrayList;

public abstract class TextGenerator {
	public String generateNumber(int[] digits) {
		if (digits.length == 0 || (digits.length == 1 && digits[0] == 0))
			return getZero();


		ArrayList<String> generatedNumber = new ArrayList<>();

		for (int zeroes = 0; zeroes < digits.length; zeroes += 4) {
			int index = digits.length - 1 - zeroes;
			generatedNumber.addAll(0, generatePart(digits, index, zeroes));
		}

		if(generatedNumber.size() == 0)
			generatedNumber.add(getZero());

		return join(generatedNumber);
	}

	private ArrayList<String> generatePart(int[] digits, int lastIndex, int zeroesAfterPart) {
		ArrayList<String> part = new ArrayList<>();
		boolean containsNonZero = false;
		boolean isOne = true;

		for (int zeroes = 0, index = lastIndex; zeroes < 4 && index >= 0; zeroes++, index--) {
			int digit = digits[index];

			if (digit < 0 || digit > 9)
				throw new IllegalArgumentException("Digits are only numbers from 0 to 9");

			if (digit == 0)
				continue;
			containsNonZero = true;

			if (zeroes != 0 || digit != 1)
				isOne = false;

			String digitString = getDigit(digit, zeroes);
			if (digitString != null && !digitString.isEmpty())
				part.add(0, digitString);
		}

		if (isOne && zeroesAfterPart != 0)
			part.clear();

		if (containsNonZero && zeroesAfterPart != 0) {
			String suffix = getPartSuffix(zeroesAfterPart);

			if (suffix == null)
				throw new IllegalArgumentException("The number is too big");

			if(!suffix.isEmpty())
				part.add(suffix);
		}

		return part;
	}

	protected String join(ArrayList<String> generatedNumber) {
		return String.join("", generatedNumber);
	}

	protected abstract String getDigit(int digit, int zeroes);

	protected abstract String getPartSuffix(int zeroes);

	protected abstract String getZero();
}
