package me.jartreg.bangou.ui;

import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.Collection;

public interface Language {
	void update(int[] digits);

	void setDisplay(OutputTextDisplay display);

	Collection<Node> getOptionNodes();
}
