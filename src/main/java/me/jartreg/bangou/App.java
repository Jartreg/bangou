package me.jartreg.bangou;

import javafx.application.Application;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.jartreg.bangou.generators.IrregularCasesGenerator;
import me.jartreg.bangou.generators.KanaGenerator;
import me.jartreg.bangou.generators.KanjiGenerator;
import me.jartreg.bangou.generators.RomajiGenerator;

public class App extends Application {
	private TextField numberInput;
	private CheckBox spacedCheckBox;
	private ChoiceBox<String> formatBox;
	private Text outputText = new Text();
	private Button copyButton;
	private ComboBox<String> fontBox;
	private double fontSize;

	@Override
	public void start(Stage primaryStage) throws Exception {
		setupControls();
		setupBindings();

		Parent root = setupLayout();
		Scene scene = new Scene(root, 500, 350);

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

		formatBox = new ChoiceBox<>(FXCollections.observableArrayList("Romaji", "Kana", "Kanji"));
		formatBox.getSelectionModel().select(0);

		fontSize = Font.getDefault().getSize() * 2.5;
		fontBox = new ComboBox<>(FXCollections.observableArrayList(Font.getFamilies()));
		fontBox.getSelectionModel().select(Font.getDefault().getName());

		copyButton = new Button("Copy");
		copyButton.setMinWidth(70);
		copyButton.setOnAction(event -> {
			ClipboardContent content = new ClipboardContent();
			content.putString(outputText.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
	}

	private Parent setupLayout() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(numberInput, 0, 0);
		numberInput.setMaxWidth(Region.USE_PREF_SIZE);
		GridPane.setHgrow(numberInput, Priority.SOMETIMES);

		grid.add(formatBox, 1, 0);
		grid.add(spacedCheckBox, 2, 0);

		grid.add(outputText, 0, 1, GridPane.REMAINING, 1);
		GridPane.setVgrow(outputText, Priority.ALWAYS);
		outputText.wrappingWidthProperty().bind(
				Bindings.createDoubleBinding(
						() -> grid.getLayoutBounds().getWidth() - 20,
						grid.layoutBoundsProperty()
				)
		);

		grid.add(fontBox, 0, 2, 2, 1);

		grid.add(copyButton, 2, 2);
		GridPane.setHalignment(copyButton, HPos.RIGHT);

		return grid;
	}

	private void setupBindings() {
		Binding<TextGenerator> generator = Bindings.createObjectBinding(() -> {
					switch (formatBox.getSelectionModel().getSelectedItem()) {
						case "Kana":
							return new KanaGenerator(spacedCheckBox.isSelected());
						case "Kanji":
							return new KanjiGenerator();
						default: //Romaji
							return new RomajiGenerator(spacedCheckBox.isSelected());
					}
				},
				formatBox.getSelectionModel().selectedItemProperty());

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

		SimpleBooleanProperty error = new SimpleBooleanProperty(false);
		StringBinding numberText = Bindings.createStringBinding(
				() -> {
					int[] digits;
					try {
						digits = Main.getDigits(numberInput.getText().trim());
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

					error.set(false);
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

		copyButton.disableProperty().bind(error.not().and(Bindings.isEmpty(numberText)));

		outputText.fontProperty().bind(
				Bindings.createObjectBinding(
						() -> new Font(fontBox.getSelectionModel().getSelectedItem(), fontSize),
						fontBox.getSelectionModel().selectedItemProperty()
				)
		);
	}
}
