package me.jartreg.bangou;

import java.util.ArrayList;

/**
 * Generates the text representation of numbers
 * @see #generateNumber(int[])
 */
public abstract class TextGenerator {
	/**
	 * Generates a number string from an array of digits.
	 * The digits are separated into groups, which are processed by {@link #generateDigitGroup(int[], int, int)}.
	 * Those groups get a suffix for their specific power and are joined together to form the number string.
	 * @param digits the digits or the number
	 * @return the number string
	 * @throws IllegalArgumentException if <code>digits</code> contains non-digits or is too long
	 */
	public String generateNumber(int[] digits) {
		// 0 or nothing
		if (digits.length == 0 || (digits.length == 1 && digits[0] == 0))
			return getZero();

		ArrayList<String> generatedNumber = new ArrayList<>();

		// seperate the digits into groups
		for (int power = 0; power < digits.length; power += 4) {
			int index = digits.length - 1 - power;
			generatedNumber.addAll(0, generateDigitGroup(digits, index, power));
		}

		// lots of zeroes? -> 0
		if (generatedNumber.size() == 0)
			generatedNumber.add(getZero());

		return join(generatedNumber);
	}

	/**
	 * Generates the text representation of a digit group.
	 * Each digit is turned into text form and a suffix is appended if the power
	 * is not 0 and the digit group contains any non-zero digits.
	 * If the digit group is 1 and the power is not 0, only the suffix is returned.
	 * @param digits the digits of th whole number
	 * @param lastIndex the index in <code>digits</code> where this group ends
	 * @param digitGroupPower the power of this digit group
	 * @return a list of "words" representing this digit group
	 * @throws IllegalArgumentException if the digit group contains non-digits or the power is too big
	 */
	private ArrayList<String> generateDigitGroup(int[] digits, int lastIndex, int digitGroupPower) {
		ArrayList<String> digitGroup = new ArrayList<>();
		boolean containsNonZero = false;
		boolean isOne = true;

		for (int power = 0, index = lastIndex; power < 4 && index >= 0; power++, index--) {
			int digit = digits[index];

			if (digit < 0 || digit > 9)
				throw new IllegalArgumentException("Digits are only numbers from 0 to 9");

			if (digit == 0)
				continue;
			containsNonZero = true;

			if (power != 0 || digit != 1)
				isOne = false;

			String digitString = getDigit(digit, power);
			if (digitString != null && !digitString.isEmpty())
				digitGroup.add(0, digitString);
		}

		if (isOne && digitGroupPower != 0)
			digitGroup.clear();

		if (containsNonZero && digitGroupPower != 0) {
			String suffix = getDigitGroupSuffix(digitGroupPower);

			if (suffix == null)
				throw new IllegalArgumentException("The number is too big");

			if (!suffix.isEmpty())
				digitGroup.add(suffix);
		}

		return digitGroup;
	}

	protected String join(ArrayList<String> generatedNumber) {
		return String.join("", generatedNumber);
	}

	/**
	 * Gets the text representation of a digit.
	 * @param digit the digit
	 * @param power the power of that digit
	 * @return <code>digit</code> as text
	 */
	protected abstract String getDigit(int digit, int power);

	/**
	 * Gets the suffix for a digit group.
	 * @param power the power of the digit group
	 * @return the suffix
	 */
	protected abstract String getDigitGroupSuffix(int power);

	/**
	 * Gets the text representation of 0.
	 * @return the text representation of 0
	 */
	protected abstract String getZero();
}
