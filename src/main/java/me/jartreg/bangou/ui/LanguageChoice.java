package me.jartreg.bangou.ui;

import java.util.function.Supplier;

public class LanguageChoice {
	private final String name;
	private final Supplier<Language> languageSupplier;
	private Language lang = null;

	public LanguageChoice(String name, Supplier<Language> languageSupplier) {
		this.name = name;
		this.languageSupplier = languageSupplier;
	}

	@Override
	public String toString() {
		return name;
	}

	public Language getLanguage() {
		if(lang == null)
			lang = languageSupplier.get();
		return lang;
	}
}
