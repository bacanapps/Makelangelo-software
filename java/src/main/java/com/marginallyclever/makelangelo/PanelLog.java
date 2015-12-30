package com.marginallyclever.makelangelo;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class PanelLog
extends JPanel
implements ActionListener, ChangeListener, KeyListener, LogListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextPane log;
	private HTMLEditorKit kit;
	private HTMLDocument doc;

	// command line
	private JPanel textInputArea;
	private JTextField commandLineText;
	private JButton commandLineSend;

	protected Translator translator;
	protected MakelangeloRobot robot;
	

	public void finalize() throws Throwable  {
		super.finalize();
		Log.removeListener(this);
	}

	
	public PanelLog(Translator _translator, MakelangeloRobot robot) {
		Log.addListener(this);
		
		translator = _translator;
		this.robot = robot;

		this.setBorder(BorderFactory.createEmptyBorder());

		// the log panel
		log = new JTextPane();
		log.setEditable(false);
		log.setBackground(Color.BLACK);
		kit = new HTMLEditorKit();
		doc = new HTMLDocument();
		log.setEditorKit(kit);
		log.setDocument(doc);
		DefaultCaret c = (DefaultCaret) log.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// Now put all the parts together
		this.setLayout(new GridBagLayout());
		GridBagConstraints con1 = new GridBagConstraints();
		con1.gridx=0;
		con1.gridy=0;
		con1.weightx=1;
		con1.weighty=1;
		con1.fill=GridBagConstraints.BOTH;
		con1.anchor=GridBagConstraints.NORTHWEST;
		this.add(new JScrollPane(log),con1);
		con1.gridy++;


		con1.fill=GridBagConstraints.HORIZONTAL;
		con1.weightx=1;
		con1.weighty=0;
		this.add(getTextInputField(),con1);
	}

	private JPanel getTextInputField() {
		textInputArea = new JPanel();
		textInputArea.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		commandLineText = new JTextField(0);
		//commandLineText.setPreferredSize(new Dimension(10, 10));
		commandLineSend = new JButton(translator.get("Send"));
		//commandLineSend.setHorizontalAlignment(SwingConstants.EAST);
		c.gridwidth=4;
		c.weightx=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridx=c.gridy=0;
		textInputArea.add(commandLineText,c);
		c.gridwidth=1;
		c.gridx=4;
		c.weightx=0;
		textInputArea.add(commandLineSend,c);

		commandLineText.addKeyListener(this);
		commandLineSend.addActionListener(this);

		//textInputArea.setMinimumSize(new Dimension(100,50));
		//textInputArea.setMaximumSize(new Dimension(10000,50));

		return textInputArea;
	}

	@Override
	public void stateChanged(ChangeEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object subject = e.getSource();

		if (subject == commandLineSend) {
			robot.sendLineToRobot(commandLineText.getText());
			commandLineText.setText("");
		}
	}


	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * Handle the key-pressed event from the text field.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * Handle the key-released event from the text field.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			robot.sendLineToRobot(commandLineText.getText());
			commandLineText.setText("");
		}
	}

	// appends a message to the log tab and system out.
	@Override
	public void logEvent(String msg) {
		// remove the
		if (msg.indexOf(';') != -1) msg = msg.substring(0, msg.indexOf(';'));

		msg = msg.replace("\n", "<br>\n") + "\n";
		msg = msg.replace("\n\n", "\n");
		try {
			kit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
			int over_length = doc.getLength() - msg.length() - 5000;
			doc.remove(0, over_length);
			//logPane.getVerticalScrollBar().setValue(logPane.getVerticalScrollBar().getMaximum());
		} catch (BadLocationException | IOException e) {
			// FIXME: failure here logs new error, causes infinite loop?
			Log.error(e.getMessage());
		}
	}

	public void clearLog() {
		try {
			doc.replace(0, doc.getLength(), "", null);
			kit.insertHTML(doc, 0, "", 0, 0, null);
			//logPane.getVerticalScrollBar().setValue(logPane.getVerticalScrollBar().getMaximum());
		} catch (BadLocationException | IOException e) {

		}
	}
}
