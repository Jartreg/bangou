package me.jartreg.bangou;

import javafx.application.Application;

public class Main {

	public static void main(String[] args) {
		Application.launch(App.class, args);
	}

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
