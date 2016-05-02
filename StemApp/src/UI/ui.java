package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import transmit.transmitter;

public class ui extends JFrame{
	/* Interactive JFrame Elements */
	private JTextPane chatbox = new JTextPane();
	private JTextField typebox = new JTextField();
	private JButton sendTypebox = new JButton("Send Text");
	private JScrollPane scroll = new JScrollPane(chatbox);
	private JScrollBar scrollbar = scroll.getVerticalScrollBar();
	private JLabel displayLabel = new JLabel();
	private JLabel baudLabel = new JLabel();
	private JCheckBox setBit0 = new JCheckBox("Bit 0");
	private JCheckBox setBit1 = new JCheckBox("Bit 1");
	private JCheckBox setBit2 = new JCheckBox("Bit 2");
	private JCheckBox setBit3 = new JCheckBox("Bit 3");
	private JCheckBox setBit4 = new JCheckBox("Bit 4");
	private JCheckBox setBit5 = new JCheckBox("Bit 5");
	private JCheckBox setBit6 = new JCheckBox("Bit 6");
	private JButton sendBitBox = new JButton("Send Bit");
	int baudmin = 0;
	int baudmax = 6;
	int baudinit = 3;
	private JSlider baudSlide = new JSlider(JSlider.HORIZONTAL, baudmin, 
											baudmax, baudinit);

	/* Menu Elements */
	private JMenuBar menuBar = new JMenuBar();
	private JMenu mainMenu = new JMenu("Settings");
	private JMenuItem comItem = new JMenuItem("Com Ports", KeyEvent.VK_C);
	
	/* Attributes for chat entries */
	private SimpleAttributeSet you = new SimpleAttributeSet();
	private SimpleAttributeSet them = new SimpleAttributeSet();
	private SimpleAttributeSet software = new SimpleAttributeSet();
	
	/* Transmitter and listener */
	private static transmitter tm;
	MultiListener send;
	
	/* Main method */
	public static void main(String args[]) {
		ui myUI = new ui();
		
		/* Close COM port when window is closed */
		myUI.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e){
				tm.closePort();
				System.out.println("Properly closed");
				System.exit(0);
			}
		});
	}
	
	/* Constructor */
	ui() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		int width = ((int) tk.getScreenSize().getWidth());
		int height = ((int) tk.getScreenSize().getHeight());
		
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel301 = new JPanel();
		JPanel panel302 = new JPanel();
		
		/* Set up Styles for Chat Entries */
		Color myGreen = new Color (39,178,11);
		Color myRed = new Color (160,4,4);
		Color myBlue = new Color (36,82,181);
		
		StyleConstants.setForeground(you, myBlue);
		StyleConstants.setBackground(you, Color.WHITE);
		StyleConstants.setBold(you, true);
		StyleConstants.setForeground(them, myRed);
		StyleConstants.setBackground(them, Color.WHITE);
		StyleConstants.setBold(them, true);
		StyleConstants.setForeground(software, myGreen);
		StyleConstants.setBackground(software, Color.WHITE);
		StyleConstants.setBold(software, true);
		
		scroll.setPreferredSize(new Dimension(width/3, height/2));
		typebox.setPreferredSize(new Dimension(width/3 - width/21 - width/28, height/30));
		sendTypebox.setPreferredSize(new Dimension(width/14, height/30));
		sendBitBox.setPreferredSize(new Dimension(width/14, height/30));
		
		displayLabel.setText("0");
		baudLabel.setText("Transmission Speed: ");
		
		chatbox.setEditable(false);
		panel.add(scroll);
		panel2.add(typebox);
		panel2.add(sendTypebox);
		
		/* Set menu properties */
		mainMenu.setMnemonic(KeyEvent.VK_S);
		mainMenu.getAccessibleContext().setAccessibleDescription(
		        "Adjust COM port settings or re-initialize");
		menuBar.add(mainMenu);
		
		comItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		comItem.getAccessibleContext().setAccessibleDescription(
		        "COM Settings");
		mainMenu.add(comItem);
		
		typebox.setDocument 
			(new JTextFieldLim(127));
		
		/* Pass UI to data transmission handler */
		tm = new transmitter(this);
		
		/* Add action listeners and set up bit controls */
		send = new MultiListener(tm);
		panel3.setLayout(new BorderLayout());
		//panel301.setLayout(new BorderLayout());
		//panel302.setLayout(new BorderLayout());
		initCheckBox(panel301, setBit6);
		initCheckBox(panel301, setBit5);
		initCheckBox(panel301, setBit4);
		initCheckBox(panel301, setBit3);
		initCheckBox(panel301, setBit2);
		initCheckBox(panel301, setBit1);
		initCheckBox(panel301, setBit0);
		panel301.add(displayLabel);
		panel301.add(Box.createHorizontalStrut(70));//creates space for ASCCI character 
		panel301.add(sendBitBox);
		
		panel302.add(baudLabel);
		panel302.add(Box.createHorizontalStrut(100));
		panel302.add(baudSlide);
		
		baudSlide.setMajorTickSpacing(1);
		baudSlide.setMinorTickSpacing(10);
		baudSlide.setPaintTicks(true);
		baudSlide.setPaintLabels(true);
		
		/* Finish creating window */
		comItem.addActionListener(send);
		this.setJMenuBar(menuBar);
		add(panel, BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		panel3.add(panel301, BorderLayout.NORTH);
		panel3.add(panel302, BorderLayout.SOUTH);
		add(panel3, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		
		/* Set up Button properties */
		typebox.addActionListener(send);
		sendTypebox.addActionListener(send);
		sendTypebox.setContentAreaFilled(false);
		sendTypebox.setBackground(myGreen);
		sendTypebox.setForeground(Color.WHITE);
		sendTypebox.setOpaque(true);
		
		sendBitBox.addActionListener(send);
		sendBitBox.setBackground(myGreen); 
		sendBitBox.setForeground(Color.WHITE);
	}
	
	/* Sets up check box */
	private void initCheckBox(JPanel panel, JCheckBox check){
		panel.add(check);
		check.setVerticalTextPosition(SwingConstants.TOP);
		check.setHorizontalTextPosition(SwingConstants.CENTER);
		check.addActionListener(send);
		send.addCheckBox(check);
		
	}
	
	/* Print sent message as "You: ..." */
	public void outMessage(String o){
		StyledDocument doc = chatbox.getStyledDocument();
		try{
			doc.insertString(doc.getLength(), "You: " + o + "\n", you);
			scrollbar.setValue(scrollbar.getMaximum());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/* Bring up COM Port Selector*/
	public void adjSettings(){
		Object[] options = tm.getPorts();
		String port = (String) JOptionPane.showInputDialog(this, 
									"Available COM Ports:", 
									"COM Settings", 
									JOptionPane.PLAIN_MESSAGE, 
									null, 
									options,
									null);
		/* Close old port and create new transmitter with selected port */
		if(port != null){
			tm.closePort();
			tm = new transmitter(this, port);
		}
	}
	
	/* Handles all GUI controls */
	private class MultiListener implements ActionListener {
		private transmitter tm;
		private final java.util.List<JCheckBox> checkBoxes = new LinkedList<JCheckBox>();
		private String currchar = "";

		/**
		 * Adds the specified JCheckBox to the list of JCheckBoxes.
		 */
		public void addCheckBox(JCheckBox checkBox) {
		    this.checkBoxes.add(checkBox);
		}
		
		/* Constructor */
		MultiListener(transmitter passed){
			tm = passed;
		}
		
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			
			/* Clear the text entry, show message as sent, and send message */
			if (src == typebox || src == sendTypebox) {
				String message = typebox.getText();
				typebox.setText("");
				outMessage(message);
				tm.sendData(message, baudSlide.getValue()); 
			}
			
			/* Update label when a checkbox is checked or unchecked */
			if(checkBoxes.contains(src)){
				StringBuilder sb = new StringBuilder();

			    // Iterate over each JCheckBox and build message ready for display.
			    for (JCheckBox checkBox : checkBoxes) {
			    	if (checkBox.isSelected()) { sb.append("1"); } 
			    	else{ sb.append("0"); }
			    }
			    String ans = sb.reverse().toString();
			    String fin = Character.toString((char) Integer.parseInt(ans, 2));
			    
			    /*Output character*/
			    displayLabel.setText(fin);
			    currchar = fin;
			}
			
			/* Send bit when send bit button is pushed */
			if(src == sendBitBox){
				outMessage(currchar);
				tm.sendData(currchar, baudSlide.getValue());
			}
			
			/* Bring up COM Settings when menu button is pressed */
			if(src == comItem) {
				adjSettings();
			}
		}
	}
	
	/* Prints string received over serial communication */
	public void printReceived(String r, boolean issoft){
		StyledDocument doc = chatbox.getStyledDocument();
		SimpleAttributeSet source = them;
		String preface = "Them: ";
		
		/* Format for software messages if necessary */
		if(issoft){
			source = software;
			preface = "Software: ";
		}
		
		try {
			if(doc.getLength()==0){
				/* r is initial message, definitely software and at 0 index */
				doc.insertString(0, preface + r, source);
				return;
			}
			if(doc.getText(doc.getLength()-1, 1).equals("\n")){ 
				/* This is the start of a new message, print preface */
				doc.insertString(doc.getLength(), preface, source);
			} else if(issoft){
				/* This is a software message, move to a newline and print preface */
				doc.insertString(doc.getLength(), "\n" + preface, source);
			}
			/* Print message and scroll down */
			doc.insertString(doc.getLength(), r, source);
			scrollbar.setValue(scrollbar.getMaximum());
		} catch (BadLocationException e1){ 
			e1.printStackTrace();
		}
	}
	
	/* Prints string received over serial communication
	 * Handles situation where "issoft" boolean is not provided */
	public void printReceived(String r) {
		printReceived(r, false);	
	}
	
	/* Enforces 127-character limit in the text box 
	 * Prevents messages from violating hardware limitations
	 */
	private class JTextFieldLim extends PlainDocument {
		private int lim;
		
		JTextFieldLim(int lim) {
			super();
			this.lim = lim;
		}
		
		public void insertString( int offset, String str, AttributeSet attr ) throws BadLocationException {
			if (str == null) return;
			
			if ((getLength() + str.length()) <= lim) {
				super.insertString(offset,  str, attr);
			}
		}
	}
}



