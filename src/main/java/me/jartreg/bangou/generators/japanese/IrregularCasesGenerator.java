package me.jartreg.bangou.generators.japanese;

import me.jartreg.bangou.TextGenerator;

import java.util.ArrayList;

/**
 * Extends <code>TextGenerator</code> to handle the irregular cases in Rōmaji and Hiragana
 * and add the ability to split the text parts with spaces.
 */
public abstract class IrregularCasesGenerator extends TextGenerator {
	private boolean spaced;

	protected IrregularCasesGenerator(boolean spaced) {
		this.spaced = spaced;
	}

	@Override
	protected String getDigit(int digit, int power) {
		switch (power) {
			case 0:
				return digit == 1 ? getOne() : getDigit(digit);
			case 1:
				return getDigit(digit) + getTen();
			case 2:
				return getHundred(digit);
			case 3:
				return getThousand(digit);
		}

		return null;
	}

	protected abstract String getDigit(int digit);

	protected abstract String getOne();

	protected abstract String getTen();

	protected abstract String getHundred(int digit);

	protected abstract String getThousand(int digit);

	@Override
	protected String join(ArrayList<String> generatedNumber) {
		return String.join(spaced ? getSpace() : "", generatedNumber);
	}

	/**
	 * Gets the space used in this writing system.
	 * @return the space character(s)
	 */
	protected abstract String getSpace();

	/**
	 * Gets whether the output is separated by spaces.
	 * @return <code>true</code>, if the output is separated by spaces
	 */
	public boolean isSpaced() {
		return spaced;
	}

	/**
	 * Sets whether the output should be separated by spaces
	 * @param spaced <code>true</code>, if th output should be separated by spaces
	 */
	public void setSpaced(boolean spaced) {
		this.spaced = spaced;
	}
}
