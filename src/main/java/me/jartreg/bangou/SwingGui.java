package me.jartreg.bangou;

import me.jartreg.bangou.generators.IrregularCasesGenerator;
import me.jartreg.bangou.generators.HiraganaGenerator;
import me.jartreg.bangou.generators.KanjiGenerator;
import me.jartreg.bangou.generators.RoumajiGenerator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

@Deprecated
public class SwingGui {
	private static final String[] OUTPUT_FORMATS = {
			"Rōmaji",
			"Hiragana",
			"Kanji"
	};

	private final JFrame frame;
	private final JTextField textField;
	private final JLabel outputLabel;
	private final JButton copyButton;

	private TextGenerator generator = new RoumajiGenerator(true);
	private boolean resetStyles = false;

	public SwingGui() {
		frame = new JFrame("Bangō");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel container = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		container.setLayout(layout);

		JCheckBox spacedCheckBox = new JCheckBox("Add spaces to improve readability");
		JComboBox<String> formatComboBox = new JComboBox<>(OUTPUT_FORMATS);
		textField = new JTextField(12);
		outputLabel = new JLabel();
		copyButton = new JButton("Copy");

		formatComboBox.setSelectedIndex(0);
		formatComboBox.addItemListener(e -> {
			switch (formatComboBox.getSelectedIndex()) {
				case 0:
					generator = new RoumajiGenerator(spacedCheckBox.isSelected());
					break;
				case 1:
					generator = new HiraganaGenerator(spacedCheckBox.isSelected());
					break;
				case 2:
					generator = new KanjiGenerator();
					break;
			}

			spacedCheckBox.setEnabled(generator instanceof IrregularCasesGenerator);
			update();
		});

		spacedCheckBox.setSelected(true);
		spacedCheckBox.addChangeListener((e) -> {
			if (generator != null && generator instanceof IrregularCasesGenerator) {
				((IrregularCasesGenerator) generator).setSpaced(spacedCheckBox.isSelected());
				update();
			}
		});

		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
		});

		outputLabel.setFont(outputLabel.getFont().deriveFont((float) outputLabel.getFont().getSize() * 2.5f));

		copyButton.addActionListener((e) -> {
			StringSelection content = new StringSelection(outputLabel.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
		});

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 0);

		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(textField, c);
		container.add(textField);

		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_END;
		layout.setConstraints(formatComboBox, c);
		container.add(formatComboBox);

		c.insets.right = 10;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(spacedCheckBox, c);
		container.add(spacedCheckBox);

		c.anchor = GridBagConstraints.CENTER;
		c.weighty = 1;
		layout.setConstraints(outputLabel, c);
		container.add(outputLabel);

		c.fill = GridBagConstraints.NONE;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_END;
		layout.setConstraints(copyButton, c);
		container.add(copyButton);

		update();
		frame.setContentPane(container);
		frame.setSize(500, 400);
	}

	public static void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {//(UnsupportedLookAndFeelException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	private void update() {
		int[] digits;
		try {
			digits = Utilities.getDigits(textField.getText().trim());
		} catch (NumberFormatException e) {
			error("This is not a valid number");
			return;
		}

		if (resetStyles) {
			textField.setBackground(UIManager.getColor("TextField.background"));
			outputLabel.setForeground(null);
			copyButton.setEnabled(true);
			resetStyles = false;
		}

		if (digits.length == 0) {
			outputLabel.setText("Enter a number");
			copyButton.setEnabled(false);
			resetStyles = true;
			return;
		}

		String number;
		try {
			number = generator.generateNumber(digits);
		} catch (IllegalArgumentException e) {
			error(e.getMessage());
			return;
		}

		outputLabel.setText(number);
	}

	private void error(String message) {
		textField.setBackground(Color.PINK);
		outputLabel.setForeground(Color.RED);
		outputLabel.setText(message);
		copyButton.setEnabled(false);

		resetStyles = true;
	}
}