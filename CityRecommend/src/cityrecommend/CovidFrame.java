package cityrecommend;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollPane;
import java.awt.Toolkit;

public class CovidFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -447747183374157956L;
	private JPanel contentPane;
	private JTextArea textArea;


	/**
	 * Create the frame.
	 */
	public CovidFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(CovidFrame.class.getResource("/graphics/WarningIcon.png")));
		setTitle("COVID Information");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 710, 668);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JScrollPane sp = new JScrollPane(textArea);
		contentPane.add(sp, BorderLayout.CENTER);
		
		
		
		
	}
	
	public void setTextAreaText(String text) {
		textArea.setText(text);
	}
	
	public void appendTextAreaText(String text) {
		textArea.append(text);
	}
	
	
	
}
