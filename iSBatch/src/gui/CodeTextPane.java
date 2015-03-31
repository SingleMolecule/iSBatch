package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CodeTextPane extends JComponent implements DocumentListener, Runnable {
	
	private static final long serialVersionUID = 1L;
	
	private JTextPane textPane = new JTextPane();
	private StyledDocument document = textPane.getStyledDocument();
	
	public CodeTextPane() {
		
		setPreferredSize(new Dimension(600, 400));
		
		// add all styles
		Style plainStyle = document.addStyle("plain", null);
		plainStyle.addAttribute(StyleConstants.FontFamily, "Monospaced");
		plainStyle.addAttribute(StyleConstants.FontSize, 15);
		plainStyle.addAttribute(StyleConstants.Foreground, Color.BLACK);
		
		Style style = document.addStyle("keyword", plainStyle);
		style.addAttribute(StyleConstants.Foreground, Color.BLUE);
		
		style = document.addStyle("comment", plainStyle);
		style.addAttribute(StyleConstants.Foreground, Color.GREEN);
		
		style = document.addStyle("identifier", plainStyle);
		style.addAttribute(StyleConstants.Foreground, Color.BLACK);
		
		style = document.addStyle("literal", plainStyle);
		style.addAttribute(StyleConstants.Foreground, Color.MAGENTA);
		
		style = document.addStyle("operator", plainStyle);
		style.addAttribute(StyleConstants.Foreground, Color.RED);
		
		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.add(textPane, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(noWrapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		
		document.addDocumentListener(this);
	}

	@Override
	public void run () {
		
		try {
			
			String text = document.getText(0, document.getLength());
			String literalRegEx = "(\\d+|\"([^\"\\\\]|(\\\\\"))*\")";
			String commentRegEx = "(//.*\\n|/\\*.*\\*/)";			
			String operatorRegEx = "(\\+\\+|\\+|--|-|!|~|\\*|/|%|&|\\||\\^|<<|>>|\\+|-|<|<=|>|>=|==|!=|&&|\\|\\||=|\\+=|-=|\\*=|/=|\\(|\\))";
			
			highlight(text, ".*", "plain");
			highlightIdentifiers(text);
			highlight(text, operatorRegEx, "operator");
			highlight(text, literalRegEx, "literal");
			highlight(text, commentRegEx, "comment");
			
		}
		catch (BadLocationException e) {
			
		}
	}
	
	protected void highlightIdentifiers(String text) {
		
		String identifierRegEx = "[a-zA-Z]\\w*";
		String keywordRegEx = "(if|else|for|do|while|function|macro|var)";
		Pattern pattern = Pattern.compile(identifierRegEx);
		Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			
			int offset = matcher.start();
			int length = matcher.end() - matcher.start();
			String identifier = matcher.group();
			Style style = document.getStyle(identifier.matches(keywordRegEx) ? "keyword" : "identifier");

			document.setCharacterAttributes(offset, length, style, true);
			
		}
		
	}
	
	protected void highlight(String text, String regEx, String styleName) {
		
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(text);
		Style style = document.getStyle(styleName);
		
		while (matcher.find()) {
			int offset = matcher.start();
			int length = matcher.end() - matcher.start();
			document.setCharacterAttributes(offset, length, style, true);
		}
		
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		System.out.println("changed update");
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		System.out.println("insert update");
		
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		System.out.println("remove update");
		
		SwingUtilities.invokeLater(this);
	}
	
	public void setText(String text) {
		textPane.setText(text);
	}
	
	public String getText() {
		return textPane.getText();
	}
	
}
