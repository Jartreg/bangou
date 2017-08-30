package me.jartreg.bangou.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class OutputTextDisplay extends BorderPane {
	private final Text textDisplay = new Text();

	private final SimpleStringProperty fontFamily = new SimpleStringProperty(this, "fontFamily", Font.getDefault().getFamily());

	private final ReadOnlyStringWrapper text = new ReadOnlyStringWrapper(this, "text");

	private final ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>(this, "state", State.Normal);

	public OutputTextDisplay() {
		textDisplay.wrappingWidthProperty().bind(Bindings.createDoubleBinding(
				() -> getLayoutBounds().getWidth(),
				layoutBoundsProperty()
		));

		textDisplay.fillProperty().bind(
				Bindings.when(state.isEqualTo(State.Error))
						.then((Paint) Color.RED)
						.otherwise(textDisplay.getFill())
		);

		double fontSize = Font.getDefault().getSize() * 2.5;
		textDisplay.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font(
						fontFamily.get(),
						state.get() == State.Hint ? FontPosture.ITALIC : FontPosture.REGULAR,
						fontSize
				),
				state, fontFamily
		));

		textDisplay.textProperty().bind(text);

		setCenter(textDisplay);
	}

	public void setText(String text) {
		state.set(State.Normal);
		this.text.set(text);
	}

	public void setHint(String hint) {
		state.set(State.Hint);
		text.set(hint);
	}

	public void setError(String error) {
		state.set(State.Error);
		text.set(error);
	}

	public String getFontFamily() {
		return fontFamily.get();
	}

	public SimpleStringProperty fontFamilyProperty() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily.set(fontFamily);
	}

	public String getText() {
		return text.get();
	}

	public ReadOnlyStringProperty textProperty() {
		return text.getReadOnlyProperty();
	}

	public State getState() {
		return state.get();
	}

	public ReadOnlyObjectProperty<State> stateProperty() {
		return state.getReadOnlyProperty();
	}

	public enum State {
		Hint, Error, Normal
	}
}
