package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.FlowLayout;

import javax.swing.JTabbedPane;

import java.awt.Panel;

import javax.swing.JList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Properties;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;

import javax.swing.SwingConstants;

import utils.AwS3Conn;
import utils.ConfigManager;

import javax.swing.JCheckBox;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ListSelectionModel;


public class MainGui extends JFrame {

	private JPanel contentPane;
	private JTextField prefix;
	private static JTextArea txtrStatus;
	private AwS3Conn awsS4 = null;
	private CardLayout navLayout;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField bucketName;
	private JTextField textField_2;
	private File uFile;
	private AmazonS3 s3;
	private ObjectListing objListing;
	private JList<String> keyList;
	private DefaultListModel<String> listModel = new DefaultListModel<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui frame = new MainGui();
					frame.setSize(640, 480);
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
	public MainGui() {
		final JFrame frame = this;
		setTitle("Amazon Secure Simple Storage Service (S4)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		final JPanel navPanel = new JPanel();
		contentPane.add(navPanel, BorderLayout.CENTER);
		navLayout = new CardLayout();
		navPanel.setLayout(navLayout);

		JPanel accessPanel = new JPanel();
		navPanel.add(accessPanel, "accessPanel");

		JPanel s3Connect = new JPanel();
		accessPanel.add(s3Connect);

		JButton defaultConnBtn = new JButton("Connect Using Default Keys");
		defaultConnBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		s3Connect.setLayout(new BorderLayout(0, 0));

		s3Connect.add(defaultConnBtn, BorderLayout.NORTH);

		JPanel panel_1 = new JPanel();
		s3Connect.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);

		JLabel lblNewLabel_1 = new JLabel("Access Key");
		panel_2.add(lblNewLabel_1);

		textField = new JTextField();
		panel_2.add(textField);
		textField.setColumns(10);

		JPanel panel_9 = new JPanel();
		panel_1.add(panel_9);

		JLabel lblSKey = new JLabel("Secret Key");
		panel_9.add(lblSKey);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		panel_9.add(textField_1);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Set As Default");
		panel_1.add(chckbxNewCheckBox);

		JButton btnNewButton_6 = new JButton("Connect Using New Keys");
		s3Connect.add(btnNewButton_6, BorderLayout.SOUTH);

		JPanel s4Panel = new JPanel();
		navPanel.add(s4Panel, "s4Panel");
		s4Panel.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		s4Panel.add(tabbedPane);

		JPanel uploadPanel = new JPanel();
		tabbedPane.addTab("Upload", null, uploadPanel, null);
		uploadPanel.setLayout(new BoxLayout(uploadPanel, BoxLayout.Y_AXIS));

		JPanel panel_7 = new JPanel();
		uploadPanel.add(panel_7);
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.X_AXIS));

		JButton fileSelectBtn = new JButton("Select File");

		panel_7.add(fileSelectBtn);

		final JLabel lblNewLabel = new JLabel("No File Selected");
		panel_7.add(lblNewLabel);

		
		
		JPanel panel_3 = new JPanel();
		uploadPanel.add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel_2 = new JLabel("File Name w/ Directory on Bucket");
		panel_3.add(lblNewLabel_2);
		
		textField_2 = new JTextField();
		panel_3.add(textField_2);
		textField_2.setColumns(10);
		
		JButton uploadBtn = new JButton("Upload");
				
		uploadPanel.add(uploadBtn);

		JPanel downloadPanel = new JPanel();
		tabbedPane.addTab("Download", null, downloadPanel, null);
		downloadPanel.setLayout(new BoxLayout(downloadPanel, BoxLayout.Y_AXIS));

		Panel panel_8 = new Panel();
		downloadPanel.add(panel_8);
		panel_8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		prefix = new JTextField();
		panel_8.add(prefix);
		prefix.setColumns(10);

		JButton searchBtn = new JButton("Search By Prefix");
		
		panel_8.add(searchBtn);

		JButton btnNewButton_4 = new JButton("Download");
		panel_8.add(btnNewButton_4);
		
		keyList = new JList<String>(listModel);
		keyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		JScrollPane scrollPane_1 = new JScrollPane(keyList);
		downloadPanel.add(scrollPane_1);

		JPanel panel = new JPanel();
		s4Panel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_10 = new JPanel();
		panel.add(panel_10);

		JLabel label = new JLabel("Bucket Name");
		panel_10.add(label);

		bucketName = new JTextField();
		bucketName.setColumns(10);
		panel_10.add(bucketName);

		JButton bucketSelectBtn = new JButton("Select/Create");

		panel_10.add(bucketSelectBtn);

		JPanel panel_4 = new JPanel();
		panel.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.X_AXIS));

		JButton btnNewButton = new JButton("Choose Existing Key Pair");
		panel_4.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Create New Key Pair");
		panel_4.add(btnNewButton_1);
		
		
		
		
		
		txtrStatus = new JTextArea(4,30);
		txtrStatus.setEditable(false);
		DefaultCaret caret = (DefaultCaret)txtrStatus.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(txtrStatus);
		
		contentPane.add(scrollPane, BorderLayout.SOUTH);

		defaultConnBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				log("Using Default Keys to connect to AWS S3");
				Properties prop = ConfigManager.getProperties();
				if (prop != null) {
					awsS4 = new AwS3Conn(prop.getProperty("access-key"), prop
							.getProperty("secret-access-key"));
					log("Connected to AWS S3");
					if (awsS4 != null) {
						navLayout.show(navPanel, "s4Panel");
						s3 = awsS4.getS3client();
					}
				}

			}
		});

		bucketSelectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(bucketName.getText());
				awsS4.setBucket(bucketName.getText());
				objListing = awsS4.getObjectListing("");
				upDateList();
				
			}

			
		});
		
		searchBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				objListing = awsS4.getObjectListing(prefix.getText());
				upDateList();
			}
		});
		
		fileSelectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser uploadFile = new JFileChooser();
				uploadFile.showOpenDialog(frame);
				
				setUploadFile(uploadFile.getSelectedFile());

			}

			private void setUploadFile(File selectedFile) {
				// TODO Auto-generated method stub
				lblNewLabel.setText(selectedFile.getName());
				uFile = selectedFile;
			}
		});
		
		uploadBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MainGui.log("Starting Upload...");
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						TransferManager tx = new TransferManager(awsS4.getCredentials());
						/*PutObjectRequest request = new PutObjectRequest(awsS4.getBucket(),uFile.getName(),uFile);
						
						request.setGeneralProgressListener(new ProgressListener() {
							
							@Override
							public void progressChanged(ProgressEvent progressEvent) {
								// TODO Auto-generated method stub
								MainGui.log("Transferred bytes: " + 
								progressEvent.getBytesTransferred());
							}
						});*/
						
						final Upload upload = tx.upload(awsS4.getBucket(),uFile.getName(),uFile);
					
						upload.addProgressListener(new ProgressListener(){

							@Override
							public void progressChanged(ProgressEvent progressEvent) {
								// TODO Auto-generated method stub
								System.out.println(upload.getProgress().getPercentTransferred() + "%");
								MainGui.log(upload.getProgress().getPercentTransferred() + "%"); 
								
						       
							}
							
						});
						
					

					}
					
				}).start();
				
				
								
				
				 /*try {
			        	// You can block and wait for the upload to finish
			        	upload.waitForCompletion();
			        } catch (AmazonClientException amazonClientException) {
			        	System.out.println("Unable to upload file, upload aborted.");
			        	amazonClientException.printStackTrace();
			        } catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
			}
		});
	}

	public static void log(String msg) {
		txtrStatus.append(msg + "\n");
	}
	
	public void upDateList() {
		// TODO Auto-generated method stub
		listModel.removeAllElements();
		for (S3ObjectSummary objectSummary : 
        	objListing.getObjectSummaries()) {
            listModel.addElement(" - " + objectSummary.getKey() + "  " +
                    "(size = " + objectSummary.getSize() + 
                    ")");
        }
	}
}
