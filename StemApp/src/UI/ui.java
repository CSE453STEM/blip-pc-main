package UI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class ui extends JFrame{
	public static void main(String args[]) {
		new ui();
		//this is a comment
	}
	ui() {
		JPanel panel = new JPanel();
		JLabel jlbHelloWorld = new JLabel("Hello World");
		add(jlbHelloWorld);
		this.setSize(100, 100);
		JTextArea chatbox = new JTextArea("");
		JTextField typebox = new JTextField("type here");
		chatbox.setPreferredSize(new Dimension(600,800));
		typebox.setPreferredSize(new Dimension(600,40));
		JButton b0 = new JButton("bit 0");
		panel.add(chatbox);
		panel.add(typebox);
		panel.add(b0);
		add(panel);
		pack();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		//String message = typebox.getText();
		
	}
}

