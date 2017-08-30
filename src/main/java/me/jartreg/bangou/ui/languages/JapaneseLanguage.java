package me.jartreg.bangou.ui.languages;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import me.jartreg.bangou.TextGenerator;
import me.jartreg.bangou.generators.HiraganaGenerator;
import me.jartreg.bangou.generators.IrregularCasesGenerator;
import me.jartreg.bangou.generators.KanjiGenerator;
import me.jartreg.bangou.generators.RoumajiGenerator;
import me.jartreg.bangou.ui.Language;
import me.jartreg.bangou.ui.OutputTextDisplay;

import java.util.Arrays;
import java.util.Collection;

public class JapaneseLanguage implements Language {
	private final ChoiceBox<String> writingSystemBox = new ChoiceBox<>(FXCollections.observableArrayList(
			"Rōmaji",
			"Hiragana",
			"Kanji"
	));

	private final CheckBox spacedCheckBox = new CheckBox("Add spaces to improve readability");

	private final Binding<TextGenerator> textGenerator;

	private OutputTextDisplay display;
	private int[] digits;

	public JapaneseLanguage() {
		writingSystemBox.setTooltip(new Tooltip("Writing system"));
		writingSystemBox.getSelectionModel().select(0);

		spacedCheckBox.setSelected(true);

		textGenerator = Bindings.createObjectBinding(
				() -> {
					switch (writingSystemBox.getSelectionModel().getSelectedItem()) {
						case "Kanji":
							return new KanjiGenerator();
						case "Hiragana":
							return new HiraganaGenerator(spacedCheckBox.isSelected());
						default: // Rōmaji
							return new RoumajiGenerator(spacedCheckBox.isSelected());
					}
				},
				writingSystemBox.getSelectionModel().selectedItemProperty()
		);
		textGenerator.addListener(observable -> update());

		spacedCheckBox.disableProperty().bind(Bindings.createBooleanBinding(
				() -> !(textGenerator.getValue() instanceof IrregularCasesGenerator),
				textGenerator
		));
		spacedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			TextGenerator generator = textGenerator.getValue();
			if (generator instanceof IrregularCasesGenerator) {
				((IrregularCasesGenerator) generator).setSpaced(newValue);
				update();
			}
		});
	}

	@Override
	public void update(int[] digits) {
		this.digits = digits;
		update();
	}

	private void update() {
		if(digits == null)
			return;

		try {
			display.setText(textGenerator.getValue().generateNumber(digits));
		} catch (IllegalArgumentException e) {
			display.setError("This number is too big");
		}
	}

	@Override
	public void setDisplay(OutputTextDisplay display) {
		this.display = display;
	}

	@Override
	public Collection<Node> getOptionNodes() {
		return Arrays.asList(writingSystemBox, spacedCheckBox);
	}
}
