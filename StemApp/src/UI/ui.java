package UI;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import transmit.transmitter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class ui extends JFrame{
	private JTextPane chatbox = new JTextPane();
	private JTextField typebox = new JTextField();
	private SimpleAttributeSet you = new SimpleAttributeSet();
	private SimpleAttributeSet them = new SimpleAttributeSet();
	private JButton sendTypebox = new JButton("Send");
	private JScrollPane scroll = new JScrollPane(chatbox);
	private static transmitter tm;
	
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
		
		Color myGreen = new Color (141,244,127);
		Color myRed = new Color (160,4,4);
		Color myBlue = new Color (36,82,181);
		
		StyleConstants.setForeground(you, myBlue);
		StyleConstants.setBackground(you, Color.WHITE);
		StyleConstants.setBold(you, true);
		StyleConstants.setForeground(them, myRed);
		StyleConstants.setBackground(them, Color.WHITE);
		StyleConstants.setBold(them, true);
		
		scroll.setPreferredSize(new Dimension(width/3, height/2));
		typebox.setPreferredSize(new Dimension(width/3 - width/21, height/30));
		sendTypebox.setPreferredSize(new Dimension(width/24, height/30));
		
		panel.add(scroll);
		panel2.add(typebox);
		panel2.add(sendTypebox);
		add(panel);
		add(panel2, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		
		/* Pass UI to data transmission handler */
		tm = new transmitter(this);
		MultiListener send = new MultiListener(tm);
		typebox.addActionListener(send);
		sendTypebox.addActionListener(send);
		
		sendTypebox.setContentAreaFilled(false);
		sendTypebox.setBackground(myGreen);
		sendTypebox.setOpaque(true);
		
		printReceived("what's up");
	}
	
	private class MultiListener implements ActionListener {
		private transmitter tm;
		MultiListener(transmitter passed){
			tm = passed;
		}
		
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			
			if (src == typebox || src == sendTypebox) {
				StyledDocument doc = chatbox.getStyledDocument();
				try {
					doc.insertString(doc.getLength(), "You: " + typebox.getText() + "\n", you);
					tm.sendData(typebox.getText());
					typebox.setText("");
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void printReceived(String r) {
		StyledDocument doc = chatbox.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), "Them: " + r + "\n", them);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
}



