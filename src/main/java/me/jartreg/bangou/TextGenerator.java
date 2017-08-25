package me.jartreg.bangou;

import java.util.ArrayList;

public abstract class TextGenerator {
	public String generateNumber(int[] digits) {
		if (digits.length == 0 || (digits.length == 1 && digits[0] == 0))
			return getZero();


		ArrayList<String> generatedNumber = new ArrayList<>();

		for (int power = 0; power < digits.length; power += 4) {
			int index = digits.length - 1 - power;
			generatedNumber.addAll(0, generateDigitGroup(digits, index, power));
		}

		if (generatedNumber.size() == 0)
			generatedNumber.add(getZero());

		return join(generatedNumber);
	}

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

	protected abstract String getDigit(int digit, int power);

	protected abstract String getDigitGroupSuffix(int power);

	protected abstract String getZero();
}
