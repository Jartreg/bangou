package me.jartreg.bangou;

public final class Utilities {
	private Utilities() {
	}

	/**
	 * Extract digits from a string into an array of integers.
	 * @param number the number string
	 * @return an array of integers representing the digits
	 * @throws NumberFormatException if <code>number</code> contains any non-digit characters
	 */
	public static int[] getDigits(String number) throws NumberFormatException {
		int[] digits = new int[number.length()];

		for (int i = 0; i < digits.length; i++) {
			char c = number.charAt(i);

			if (!Character.isDigit(c))
				throw new NumberFormatException();
			digits[i] = Character.digit(c, 10);
		}

		return digits;
	}
}
