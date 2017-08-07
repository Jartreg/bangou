package me.jartreg.bangou.generators;

public class RoumajiGenerator extends IrregularCasesGenerator {
	private static final String[] DIGITS = {
			"",
			"",
			"ni",
			"san",
			"yon",
			"go",
			"roku",
			"nana",
			"hachi",
			"kyū"
	};

	public RoumajiGenerator(boolean spaced) {
		super(spaced);
	}

	@Override
	public String getDigit(int digit) {
		return DIGITS[digit];
	}

	@Override
	public String getZero() {
		return "rei";
	}

	@Override
	protected String getOne() {
		return "ichi";
	}

	@Override
	protected String getTen() {
		return "jū";
	}

	@Override
	public String getHundred(int digit) {
		switch (digit) {
			case 3:
				return "sanbyaku";
			case 6:
				return "roppyaku";
			case 8:
				return "happyaku";
		}

		return getDigit(digit) + "hyaku";
	}

	@Override
	public String getThousand(int digit) {
		switch (digit) {
			case 3:
				return "sanzen";
			case 8:
				return "hassen";
		}

		return getDigit(digit) + "sen";
	}

	@Override
	protected String getPartSuffix(int zeroes) {
		switch (zeroes) {
			case 4:
				return "man";
			case 8:
				return "oku";
		}

		return null;
	}

	@Override
	protected String getSpace() {
		return " ";
	}
}
