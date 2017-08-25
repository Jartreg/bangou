package me.jartreg.bangou.generators;

import me.jartreg.bangou.TextGenerator;

import java.util.ArrayList;

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

	protected abstract String getSpace();

	public boolean isSpaced() {
		return spaced;
	}

	public void setSpaced(boolean spaced) {
		this.spaced = spaced;
	}
}
