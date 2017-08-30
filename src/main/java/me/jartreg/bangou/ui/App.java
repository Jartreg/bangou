package me.jartreg.bangou.ui;

import javafx.application.Application;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import me.jartreg.bangou.Utilities;
import me.jartreg.bangou.ui.languages.JapaneseLanguage;

public class App extends Application {
	private TextField numberInput;
	private ChoiceBox<LanguageChoice> languageBox;

	private ToolBar languageOptions = new ToolBar();

	private OutputTextDisplay textDisplay = new OutputTextDisplay();

	private ComboBox<String> fontBox;
	private Button copyButton;

	@Override
	public void start(Stage primaryStage) throws Exception {
		setupControls();
		setupBindings();

		languageBox.getSelectionModel().select(0);
		textDisplay.setHint("Enter a number");

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

		languageBox = new ChoiceBox<>(setupLanguageChoices());
		languageBox.setTooltip(new Tooltip("Language"));

		fontBox = new ComboBox<>(FXCollections.observableArrayList(Font.getFamilies()));
		fontBox.getSelectionModel().select(Font.getDefault().getName());
		fontBox.setTooltip(new Tooltip("Font"));

		copyButton = new Button("Copy");
		copyButton.setMinWidth(70);
		copyButton.setOnAction(event -> { // copy the number to the clipboard
			ClipboardContent content = new ClipboardContent();
			content.putString(textDisplay.getText());
			Clipboard.getSystemClipboard().setContent(content);
		});
	}

	private ObservableList<LanguageChoice> setupLanguageChoices() {
		return FXCollections.observableArrayList(
				new LanguageChoice("Japanese", JapaneseLanguage::new)
		);
	}

	private Pane setupLayout() {
		GridPane root = new GridPane();
		Insets spacing = new Insets(10);

		ColumnConstraints endColumn = new ColumnConstraints();
		endColumn.setHgrow(Priority.SOMETIMES);
		endColumn.setHalignment(HPos.RIGHT);
		root.getColumnConstraints().setAll(new ColumnConstraints(), endColumn);

		BorderPane upperBar = new BorderPane(null, null, languageBox, null, numberInput);
		upperBar.getStyleClass().setAll("tool-bar");
		root.add(upperBar, 0, 0, GridPane.REMAINING, 1);

		languageOptions.setBackground(null);
		languageOptions.setPadding(spacing);
		root.add(languageOptions, 0, 1, GridPane.REMAINING, 1);

		GridPane.setMargin(textDisplay, spacing);
		GridPane.setVgrow(textDisplay, Priority.ALWAYS);
		root.add(textDisplay, 0, 2, GridPane.REMAINING, 1);

		GridPane.setMargin(fontBox, new Insets(10, 0, 10, 10));
		root.add(fontBox, 0, 3);

		GridPane.setMargin(copyButton, new Insets(10));
		root.add(copyButton, 1, 3);

		return root;
	}

	private void setupBindings() {
		Binding<Language> language = Bindings.createObjectBinding(
				() -> {
					LanguageChoice lang = languageBox.getSelectionModel().getSelectedItem();
					if (lang == null)
						return null;
					return lang.getLanguage();
				},
				languageBox.getSelectionModel().selectedItemProperty()
		);

		language.addListener((observable, oldValue, newValue) -> {
			if (oldValue != null)
				oldValue.setDisplay(null);

			if (newValue != null) {
				languageOptions.getItems().setAll(newValue.getOptionNodes());
				newValue.setDisplay(textDisplay);

				updateText(numberInput.getText(), newValue);
			} else {
				languageOptions.getItems().clear();
				textDisplay.setHint("Select a language");
			}
		});

		languageOptions.visibleProperty().bind(Bindings.createBooleanBinding(
				() -> !languageOptions.getItems().isEmpty(),
				languageOptions.getItems()
		));
		languageOptions.managedProperty().bind(languageOptions.visibleProperty());

		numberInput.textProperty().addListener((observable, oldValue, newValue) -> {
			Language lang = language.getValue();
			if (lang != null)
				updateText(newValue, lang);
		});

		copyButton.disableProperty().bind(textDisplay.stateProperty().isNotEqualTo(OutputTextDisplay.State.Normal));

		textDisplay.fontFamilyProperty().bind(fontBox.getSelectionModel().selectedItemProperty());
	}

	private void updateText(String number, Language lang) {
		number = number.trim();
		if (number.isEmpty()) {
			textDisplay.setHint("Enter a number");
			lang.update(null);
			return;
		}

		int[] digits = null;
		try {
			digits = Utilities.getDigits(number);
		} catch (NumberFormatException e) {
			textDisplay.setError("This is not a valid number");
		}

		lang.update(digits);
	}
}
