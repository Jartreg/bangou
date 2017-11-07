package me.jartreg.bangou.generators.japanese;

public class HiraganaGenerator extends IrregularCasesGenerator {
	private static final String[] DIGITS = {
			"",
			"",
			"に",
			"さん",
			"よん",
			"ご",
			"ろく",
			"なな",
			"はち",
			"きゅう"
	};

	public HiraganaGenerator(boolean spaced) {
		super(spaced);
	}

	@Override
	public String getDigit(int digit) {
		return DIGITS[digit];
	}

	@Override
	public String getZero() {
		return "れい";
	}

	@Override
	protected String getOne() {
		return "いち";
	}

	@Override
	public String getTen() {
		return "じゅう";
	}

	@Override
	public String getHundred(int digit) {
		switch (digit) {
			case 3:
				return "さんびゃく";
			case 6:
				return "ろっぴゃく";
			case 8:
				return "はっぴゃく";
		}

		return DIGITS[digit] + "ひゃく";
	}

	@Override
	public String getThousand(int digit) {
		switch (digit) {
			case 3:
				return "さんぜん";
			case 8:
				return "はっせん";
		}

		return DIGITS[digit] + "せん";
	}

	@Override
	protected String getDigitGroupSuffix(int power) {
		switch (power) {
			case 4:
				return "まん";
			case 8:
				return "おく";
		}

		return null;
	}

	@Override
	protected String getSpace() {
		return "　";
	}
}
