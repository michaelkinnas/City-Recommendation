package cityrecommend;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;

public class DateAdded extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4942055064831402536L;
	private JPanel contentPane;
	private JTable table;	
	private JScrollPane scroll;
	
	

	/**
	 * Create the frame.
	 */
	public DateAdded(Object[][] data) {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);		
		
		String[] columnNames = {"Day", "City"};		
		
		table = new JTable(data, columnNames);
		table.setEnabled(false);
		table.setShowGrid(false);
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		scroll = new JScrollPane(table);
				
		contentPane.add(scroll, BorderLayout.CENTER);
	}
	

}
