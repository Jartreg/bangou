package me.jartreg.bangou;

import javafx.application.Application;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.jartreg.bangou.generators.IrregularCasesGenerator;
import me.jartreg.bangou.generators.HiraganaGenerator;
import me.jartreg.bangou.generators.KanjiGenerator;
import me.jartreg.bangou.generators.RoumajiGenerator;

public class App extends Application {
	private TextField numberInput;
	private ChoiceBox<String> formatBox;
	private CheckBox spacedCheckBox;

	private Text outputText = new Text();

	private ComboBox<String> fontBox;
	private Button copyButton;

	private double fontSize;

	@Override
	public void start(Stage primaryStage) throws Exception {
		setupControls();
		setupBindings();

		Pane root = setupLayout();
		Scene scene = new Scene(root, Region.USE_PREF_SIZE, 350);

		primaryStage.setTitle("Bangō");
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(500);
		primaryStage.setMinHeight(300);
		primaryStage.show();
	}

	private void setupControls() {
		numberInput = new TextField();
		numberInput.setPromptText("Enter a number");

		spacedCheckBox = new CheckBox("Add spaces to improve readability");
		spacedCheckBox.setSelected(true);

		formatBox = new ChoiceBox<>(FXCollections.observableArrayList("Rōmaji", "Hiragana", "Kanji"));
		formatBox.getSelectionModel().select(0);
		formatBox.setTooltip(new Tooltip("Writing system"));

		fontSize = Font.getDefault().getSize() * 2.5;
		fontBox = new ComboBox<>(FXCollections.observableArrayList(Font.getFamilies()));
		fontBox.getSelectionModel().select(Font.getDefault().getName());
		fontBox.setTooltip(new Tooltip("Font"));

		copyButton = new Button("Copy");
		copyButton.setMinWidth(70);
		copyButton.setOnAction(event -> { // copy the number to the clipboard
			ClipboardContent content = new ClipboardContent();
			content.putString(outputText.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
	}

	private Pane setupLayout() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);

		// first row
		grid.add(numberInput, 0, 0);
		numberInput.setMaxWidth(Region.USE_PREF_SIZE);
		GridPane.setHgrow(numberInput, Priority.SOMETIMES);

		grid.add(formatBox, 1, 0);
		grid.add(spacedCheckBox, 2, 0);

		// second row (middle)
		grid.add(outputText, 0, 1, GridPane.REMAINING, 1);
		GridPane.setVgrow(outputText, Priority.ALWAYS); // fill the remaining vertical space
		outputText.wrappingWidthProperty().bind( // wrap the text when it overflows the grid's width
				Bindings.createDoubleBinding( // text width = grid width - padding left and right
						() -> grid.getLayoutBounds().getWidth() - 20,
						grid.layoutBoundsProperty()
				)
		);

		// last row
		grid.add(fontBox, 0, 2, 2, 1);

		grid.add(copyButton, 2, 2);
		GridPane.setHalignment(copyButton, HPos.RIGHT);

		return grid;
	}

	private void setupBindings() {
		// The generator depends on the selected output
		Binding<TextGenerator> generator = Bindings.createObjectBinding(() -> {
					switch (formatBox.getSelectionModel().getSelectedItem()) {
						case "Hiragana":
							return new HiraganaGenerator(spacedCheckBox.isSelected());
						case "Kanji":
							return new KanjiGenerator();
						default: //Rōmaji
							return new RoumajiGenerator(spacedCheckBox.isSelected());
					}
				},
				formatBox.getSelectionModel().selectedItemProperty()
		);

		// only Rōmaji and Hiragana support spacing
		spacedCheckBox.disableProperty().bind(
				Bindings.createBooleanBinding(
						() -> !(generator.getValue() instanceof IrregularCasesGenerator),
						generator
				)
		);

		spacedCheckBox.selectedProperty().addListener(observable -> {
			TextGenerator gen = generator.getValue();
			if (gen instanceof IrregularCasesGenerator)
				((IrregularCasesGenerator) gen).setSpaced(spacedCheckBox.isSelected());
		});

		// true when there's an error
		SimpleBooleanProperty error = new SimpleBooleanProperty(false);

		// update the text when the input, the generator or the spacing has changed
		StringBinding numberText = Bindings.createStringBinding(
				() -> {
					int[] digits;
					try {
						digits = Utilities.getDigits(numberInput.getText().trim());
					} catch (NumberFormatException e) {
						error.set(true);
						return "This is not a valid number";
					}

					String number = null;
					try {
						if (digits.length > 0)
							number = generator.getValue().generateNumber(digits);
					} catch (IllegalArgumentException e) {
						error.set(true);
						return e.getMessage();
					}

					error.set(false); // success
					return number;
				},
				numberInput.textProperty(),
				generator,
				spacedCheckBox.selectedProperty()
		);

		outputText.textProperty().bind(
				Bindings.when(Bindings.isEmpty(numberText))
						.then("Enter a number")
						.otherwise(numberText)
		);

		outputText.fillProperty().bind(
				Bindings.when(error)
						.then((Paint) Color.RED)
						.otherwise(outputText.getFill())
		);

		// disabled when there's an error or no text at all
		copyButton.disableProperty().bind(error.or(Bindings.isEmpty(numberText)));

		outputText.fontProperty().bind(
				Bindings.createObjectBinding(
						() -> new Font(fontBox.getSelectionModel().getSelectedItem(), fontSize),
						fontBox.getSelectionModel().selectedItemProperty()
				)
		);
	}
}
