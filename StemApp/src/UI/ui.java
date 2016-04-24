package UI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.PlainDocument;

import transmit.transmitter;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

public class ui extends JFrame{
	private JTextPane chatbox = new JTextPane();
	private JTextField typebox = new JTextField();
	private SimpleAttributeSet you = new SimpleAttributeSet();
	private SimpleAttributeSet them = new SimpleAttributeSet();
	private SimpleAttributeSet software = new SimpleAttributeSet();
	private JButton sendTypebox = new JButton("Send");
	private JScrollPane scroll = new JScrollPane(chatbox);
	private JScrollBar scrollbar = scroll.getVerticalScrollBar();
	private static transmitter tm;
	
	private JLabel displayLabel = new JLabel();
	
	private JCheckBox setBit0 = new JCheckBox("Bit 0");
	private JCheckBox setBit1 = new JCheckBox("Bit 1");
	private JCheckBox setBit2 = new JCheckBox("Bit 2");
	private JCheckBox setBit3 = new JCheckBox("Bit 3");
	private JCheckBox setBit4 = new JCheckBox("Bit 4");
	private JCheckBox setBit5 = new JCheckBox("Bit 5");
	private JCheckBox setBit6 = new JCheckBox("Bit 6");
	private JButton sendBitBox = new JButton("Send");
	private ButtonGroup bGroup = new ButtonGroup();
	MultiListener send;
	
	
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
	
	ui() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		int width = ((int) tk.getScreenSize().getWidth());
		int height = ((int) tk.getScreenSize().getHeight());
		
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		
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
		typebox.setPreferredSize(new Dimension(width/3 - width/21, height/30));
		sendTypebox.setPreferredSize(new Dimension(width/24, height/30));
		sendBitBox.setPreferredSize(new Dimension(width/26, height/32));
		
		displayLabel.setText("...");
		
		sendBitBox.setBackground(myRed); 
		
		chatbox.setEditable(false);
		panel.add(scroll);
		panel2.add(typebox);
		panel2.add(sendTypebox);
		
		typebox.setDocument 
			(new JTextFieldLim(127));
		
		/* Pass UI to data transmission handler */
		tm = new transmitter(this);
		send = new MultiListener(tm);
		sendBitBox.addActionListener(send);
		initCheckBox(panel3, setBit6);
		initCheckBox(panel3, setBit5);
		initCheckBox(panel3, setBit4);
		initCheckBox(panel3, setBit3);
		initCheckBox(panel3, setBit2);
		initCheckBox(panel3, setBit1);
		initCheckBox(panel3, setBit0);
		
		panel3.add(sendBitBox,BorderLayout.EAST);
		panel3.add(Box.createHorizontalStrut(100));//creates space for ASCCI character 
		panel3.add(displayLabel);
		
		add(panel,BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		add(panel3, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		
		typebox.addActionListener(send);
		sendTypebox.addActionListener(send);
		sendTypebox.setContentAreaFilled(false);
		sendTypebox.setBackground(myGreen);
		sendTypebox.setOpaque(true);
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
	
	/* Handles sending messages */
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
				tm.sendData(message); 
			}
			
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
			
			if(src == sendBitBox){
				outMessage(currchar);
				tm.sendData(currchar);
			}
		}
	}
	
	/* Prints string received over serial communication */
	public void printReceived(String r) {
		StyledDocument doc = chatbox.getStyledDocument();
		try {
			if(doc.getLength()==0){ //r is initial, hardcoded message
				doc.insertString(0, "Software: " + r + "\n", software);
				return;
			}
			if(doc.getText(doc.getLength()-1, 1).equals("\n")){ //Start of new message
				doc.insertString(doc.getLength(), "Them: ", them);
			}
			doc.insertString(doc.getLength(), r, them); //print received character
			scrollbar.setValue(scrollbar.getMaximum());
		} catch (BadLocationException e1){ 
			e1.printStackTrace();
		}	
	}
	
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



