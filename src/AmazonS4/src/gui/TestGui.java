package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.CardLayout;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestGui extends JFrame {

	private JPanel contentPane;
	private CardLayout c;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestGui frame = new TestGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TestGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		final JTextArea textArea = new JTextArea();
		contentPane.add(textArea, BorderLayout.SOUTH);
		
		final JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		c = new CardLayout();
		panel.setLayout(c);
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "first");
		
		JLabel lblHe = new JLabel("he");
		panel_1.add(lblHe);
		
		JButton btnNext = new JButton("next");
		
		panel_1.add(btnNext);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, "second");
		
		JLabel lblDf = new JLabel("df");
		panel_2.add(lblDf);
		
		JButton btnBack = new JButton("back");
		panel_2.add(btnBack);
		
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				c.show(panel, "secon");
				textArea.append("swit");
			}
		});
	}
}
