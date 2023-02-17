import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

import java.awt.GraphicsEnvironment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

class TextEditor extends JFrame {

    JMenuBar menuBar=new JMenuBar();
    JMenu file=new JMenu("File");
	JMenu format=new JMenu("Format");
	JMenu help=new JMenu("Help");
    JMenuItem open=new JMenuItem("Open");
    JMenuItem save=new JMenuItem("Save");
	JMenuItem wordWrap=new JMenuItem("Word Wrap unchecked");
	JMenuItem needHelp=new JMenuItem("Need Help");
    JFileChooser fileChooser=new JFileChooser();

    JPanel topPanel=new JPanel();
    JButton colorButton=new JButton("Pick Color");
    JColorChooser colorChooser=new JColorChooser();
    JComboBox<String> fonts=new JComboBox<String>();
    JSpinner fontSize=new JSpinner();
    JButton runCode=new JButton("Compile & Run");

    JPanel centerPanel=new JPanel();
    JTextArea textArea=new JTextArea();
    JScrollPane scrollPane=new JScrollPane(textArea);

    GridBagConstraints c=new GridBagConstraints();

    Font jetBrains=new Font("JetBrains Mono", Font.PLAIN, 14);
    FileNameExtensionFilter filter=new FileNameExtensionFilter(
			"Text Files", "txt", "java", "cpp", "doc", "class");

    FileReader reader;
    FileWriter writer;
    FileWriter batchWriter;

    int tabCount=0;
	boolean wordWrapped=false;

    GraphicsEnvironment gE=GraphicsEnvironment.getLocalGraphicsEnvironment();

    TextEditor() {
		this.setSize(600, 450);
		this.setLocationRelativeTo(null);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle("Text Editor & Compiler");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		setMenuBar();
		setTopPanel();
		setCenterPanel();

		this.setVisible(true);
	}

	public void helpMenuItemAction(ActionEvent e) {
		JOptionPane.showMessageDialog(this,
				"You just need to setup JDK in your system and use this as a Java Compiler",
				"Instructions", JOptionPane.INFORMATION_MESSAGE
		);
	}

    public void setMenuBar() {
		fileChooser.setFileFilter(filter);
		fileChooser.setCurrentDirectory(new File("C:/Users/Junaid/Desktop"));

		open.addActionListener(this::openMenuItemAction);
		save.addActionListener(this::saveMenuItemAction);
		wordWrap.addActionListener(this::wordWrapAction);
		needHelp.addActionListener(this::helpMenuItemAction);

		file.add(open);
		file.add(save);
		format.add(wordWrap);
		help.add(needHelp);

		menuBar.add(file);
		menuBar.add(format);
		menuBar.add(help);
		this.setJMenuBar(menuBar);
	}

    public void setTopPanel() {
		c.insets = new Insets(10, 15, 10, 15);
		topPanel.setLayout(new GridBagLayout());

		String[] fontNames = gE.getAvailableFontFamilyNames();
		for (String i : fontNames) fonts.addItem(i);
		fonts.setSelectedItem("JetBrains Mono");
		fonts.setFont(jetBrains);
		topPanel.add(fonts, c);

		fontSize.setModel(new SpinnerNumberModel(14, 10, 50, 2));
		fontSize.setFont(jetBrains);
		fontSize.setPreferredSize(new Dimension(100, 30));
		topPanel.add(fontSize, c);

		colorButton.setFont(jetBrains);
		topPanel.add(colorButton, c);

		fonts.addActionListener(this::fontsAction);
		colorButton.addActionListener(this::colorButtonAction);
		fontSize.addChangeListener(this::fontSizeAction);

		runCode.addActionListener(this::runCodeAction);
		runCode.setPreferredSize(new Dimension(150, 30));
		runCode.setFont(jetBrains);
		topPanel.add(runCode, c);

		this.add(topPanel, BorderLayout.NORTH);
	}

    public void setCenterPanel() {
		textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, (int)fontSize.getValue()));
		textArea.setLineWrap(wordWrapped);
		textArea.setTabSize(4);

		textArea.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\b') {
					if (textArea.getText().length() < 2) return;
					else if (textArea.getText().charAt(textArea.getText().length() - 1) == '\t' ||
							textArea.getText().charAt(textArea.getText().length() - 1) == '\n')
						if (tabCount > 0) tabCount--;
				} else if (e.getKeyChar() == '\t') tabCount++;
				else if (e.getKeyChar() == '\n') for (int i = 1; i <= tabCount; i++) textArea.append("\t");
			}
		});

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		centerPanel.setLayout(new CardLayout());
		centerPanel.add(scrollPane);
		this.add(centerPanel, BorderLayout.CENTER);
	}

    public void openMenuItemAction(ActionEvent e) {
		fileChooser.showOpenDialog(this);
		String path = fileChooser.getSelectedFile().getPath();

		if (fileChooser.getSelectedFile() == null) return;

		try {
			reader = new FileReader(path);
			String text = "";

			int character;
			while ((character = reader.read()) != -1)
				text += (char) character;

			textArea.setText(text);
			reader.close();
		}
		catch (FileNotFoundException ex) {}
		catch (IOException ex) {}
	}

	public void writerOnFile(String path) {
		try {
			writer = new FileWriter(path);
			writer.write(textArea.getText());
			writer.close();
		}
		catch (FileNotFoundException ex) {}
		catch (IOException ex) {}
	}

    public void saveMenuItemAction(ActionEvent e) {

		fileChooser.showSaveDialog(this);
		String path=fileChooser.getSelectedFile().getPath();

		if (fileChooser.getSelectedFile() == null) return;
		writerOnFile(path);
	}

	public void wordWrapAction(ActionEvent e) {
		if(wordWrapped) {
			textArea.setLineWrap(false);
			wordWrap.setText("Word wrap unchecked");
			wordWrapped=false;
		}
		else  {
			textArea.setLineWrap(true);
			wordWrap.setText("Word wrap checked");
			wordWrapped=true;
		}
	}

    public void fontsAction(ActionEvent e) {
		textArea.setFont(new Font((String)fonts.getSelectedItem(),
				Font.PLAIN, (int)textArea.getFont().getSize()));
    }

    public void colorButtonAction(ActionEvent e) {
		Color color = colorChooser.showDialog(null,
				"Pick a Color", textArea.getForeground());
		textArea.setForeground(color);
    }

    public void fontSizeAction(ChangeEvent e) {
		textArea.setFont(new Font((String) textArea.getFont().getFontName(),
				Font.PLAIN, (int)fontSize.getValue()));
    }

    public void runCodeAction(ActionEvent e) {

		if(fileChooser.getSelectedFile()==null) {
			saveMenuItemAction(e);
		} else {
			writerOnFile(fileChooser.getSelectedFile().getPath());
		}

		String fileName = fileChooser.getSelectedFile().getName();
		String parent=fileChooser.getSelectedFile().getParent();

		Runtime rt = Runtime.getRuntime();

		try {
			batchWriter = new FileWriter("BatchFile.bat");
			batchWriter.write(
				"@ECHO OFF\n" +
				"title " + fileName.substring(0, fileName.length() - 5) + "\n" +
				"javac " + parent + "\\" + fileName + "\n" +
				"java -cp " + parent + " " + fileName.substring(0, fileName.length() - 5) + "\n" +
				"pause\n" +
				"exit"
			);
			batchWriter.close();

			rt.exec(new String[]{"cmd.exe", "/c",
					"start BatchFile.bat"});
			rt.exec(new String[]{"exit"});
		} catch (IOException ex) {}
	}
}