package utils.logging;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class LogGui {
	//UI element
	private static JFrame frame = new JFrame("Program Output");
	private static JTextPane text = new JTextPane();
	private static JScrollPane scroll = new JScrollPane(text);

	public LogGui()	{
		//frame.setSize(700, 400);
		frame.setResizable(false);
		frame.setBounds(150, 350, 700, 400);
		frame.setFocusable(false);

		scroll.setSize(150, 350);
		frame.add(scroll);

		text.setAutoscrolls(true);
		text.setEditable(false);
		frame.setVisible(true);

	}

	public static void addMessage(String message)	{
		text.setText(text.getText() + message + "\n");

		text.setCaretPosition(text.getDocument().getLength());
	}
}
