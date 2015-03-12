package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.DefaultListModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.FlowLayout;

import javax.swing.JTabbedPane;

import java.awt.Panel;

import javax.swing.JList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import utils.AwS3Conn;
import utils.ConfigManager;
import utils.EncryptionManager;

import javax.swing.JCheckBox;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JRadioButton;

public class MainGui extends JFrame {

	private JPanel contentPane;
	private JTextField prefix;
	private static JTextArea txtrStatus;
	private AwS3Conn awsS4 = null;
	private CardLayout navLayout;
	private JTextField accessKey;
	private JTextField secretKey;
	private JTextField bucketName;
	private JTextField keyField;
	private File uFile;
	private AmazonS3 s3;
	private ObjectListing objListing;
	private JList<String> keyList;
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private KeyPair useKeys;
	private SecretKey aes_key;
	public File pub_file,priv_file ;

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

		txtrStatus = new JTextArea();
		contentPane.add(txtrStatus, BorderLayout.SOUTH);

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

		accessKey = new JTextField();
		panel_2.add(accessKey);
		accessKey.setColumns(10);

		JPanel panel_9 = new JPanel();
		panel_1.add(panel_9);

		JLabel lblSKey = new JLabel("Secret Key");
		panel_9.add(lblSKey);

		secretKey = new JTextField();
		secretKey.setColumns(10);
		panel_9.add(secretKey);

		final JCheckBox setDefault = new JCheckBox("Set As Default");
		panel_1.add(setDefault);

		JButton newConnBtn = new JButton("Connect Using New Keys");

		s3Connect.add(newConnBtn, BorderLayout.SOUTH);

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
		panel_7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		final JRadioButton fileSelectRadio = new JRadioButton("");
		panel_7.add(fileSelectRadio);

		JButton fileSelectBtn = new JButton("Select File");

		panel_7.add(fileSelectBtn);

		final JLabel lblNewLabel = new JLabel("No File Selected");
		panel_7.add(lblNewLabel);

		JPanel panel_5 = new JPanel();
		uploadPanel.add(panel_5);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JRadioButton dirSelectRadio = new JRadioButton("");
		panel_5.add(dirSelectRadio);

		ButtonGroup group = new ButtonGroup();
		group.add(fileSelectRadio);
		group.add(dirSelectRadio);

		JButton dirSelectBtn = new JButton("Select Directory");

		panel_5.add(dirSelectBtn);

		final JLabel lblNewLabel_3 = new JLabel("No Directory Selected");
		panel_5.add(lblNewLabel_3);

		JPanel panel_3 = new JPanel();
		uploadPanel.add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblNewLabel_2 = new JLabel("Key/Virtual Key Directory");
		panel_3.add(lblNewLabel_2);

		keyField = new JTextField();
		panel_3.add(keyField);
		keyField.setColumns(10);

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

		JButton downloadBtn = new JButton("Download");

		panel_8.add(downloadBtn);

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

		JButton existingBtn = new JButton("Choose Existing Key Pair");
		panel_4.add(existingBtn);

		JButton newBtn = new JButton("Create New Key Pair");
		panel_4.add(newBtn);

		txtrStatus = new JTextArea(4, 30);
		txtrStatus.setEditable(false);
		DefaultCaret caret = (DefaultCaret) txtrStatus.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(txtrStatus);

		contentPane.add(scrollPane, BorderLayout.SOUTH);

		existingBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser newRsa_pubKey = new JFileChooser();
				FileNameExtensionFilter pubkey_filter = new FileNameExtensionFilter(
						"Pubkey filter", "pubs4");
				newRsa_pubKey.setFileFilter(pubkey_filter);

				JFileChooser newRsa_privKey = new JFileChooser();
				FileNameExtensionFilter privkey_filter = new FileNameExtensionFilter(
						"Privkey filter", "privs4");
				newRsa_privKey.setFileFilter(privkey_filter);
				newRsa_pubKey
						.setDialogTitle("Enter the location to open the public key ");
				newRsa_pubKey.showOpenDialog(frame);
				newRsa_privKey
						.setDialogTitle("Enter the location to open the private key ");
				newRsa_privKey.showOpenDialog(frame);
				
				if( !newRsa_pubKey.getSelectedFile().getName().toString().contains(".pubs4") )
					pub_file = new File(newRsa_pubKey.getSelectedFile().getAbsolutePath()+".pubs4");
				else
					pub_file = newRsa_pubKey.getSelectedFile();
				
				if ( !newRsa_privKey.getSelectedFile().getName().toString().contains(".privs4") )
					priv_file = new File(newRsa_privKey.getSelectedFile().getAbsolutePath()+".privs4");
				else
					priv_file = newRsa_privKey.getSelectedFile();

				try {
					useKeys = EncryptionManager.open_keys(
							pub_file, priv_file);

					// File temp = new File("/tmp/encrypt.temps4");
					// encrypt(aes_key, useKeys);
					// upload encrypted file i.e. temp to
					// decrypt(aes_key, useKeys);
					// temp.delete();

				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidKeySpecException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		newBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser newRsa_pubKey = new JFileChooser();
				FileNameExtensionFilter pubkey_filter = new FileNameExtensionFilter(
						"Pubkey filter", "pubs4");
				newRsa_pubKey.setFileFilter(pubkey_filter);

				JFileChooser newRsa_privKey = new JFileChooser();
				FileNameExtensionFilter privkey_filter = new FileNameExtensionFilter(
						"Privkey filter", "privs4");
				newRsa_privKey.setFileFilter(privkey_filter);
				newRsa_pubKey
						.setDialogTitle("Enter the location to store the public key ");
				newRsa_pubKey.showSaveDialog(frame);
				newRsa_privKey
						.setDialogTitle("Enter the location to store the private key ");
				newRsa_privKey.showSaveDialog(frame);
				
				if( !newRsa_pubKey.getSelectedFile().getName().toString().contains(".pubs4") )
					pub_file = new File(newRsa_pubKey.getSelectedFile().getAbsolutePath()+".pubs4");
				else
					pub_file = newRsa_pubKey.getSelectedFile();
				
				if ( !newRsa_privKey.getSelectedFile().getName().toString().contains(".privs4") )
					priv_file = new File(newRsa_privKey.getSelectedFile().getAbsolutePath()+".privs4");
				else
					priv_file = newRsa_privKey.getSelectedFile();

				try {

					EncryptionManager.generate_rsa_keys(pub_file, priv_file);

					useKeys = EncryptionManager.open_keys(pub_file, priv_file);

				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidKeySpecException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		defaultConnBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				log("Using Default Keys to connect to AWS S3");
				Properties prop = ConfigManager.getProperties();
				if (prop != null) {
					awsS4 = new AwS3Conn(prop.getProperty("access-key"), prop
							.getProperty("secret-access-key"));

					if (awsS4 != null) {
						log("Connected to AWS S3");
						navLayout.show(navPanel, "s4Panel");
						s3 = awsS4.getS3client();
					}
				}

			}
		});

		newConnBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				log("Using New Keys to connect to AWS S3");
				String accKey = accessKey.getText();
				String secKey = secretKey.getText();
				awsS4 = new AwS3Conn(accKey, secKey);

				if (awsS4 != null) {
					log("Connected to AWS S3");
					navLayout.show(navPanel, "s4Panel");
					s3 = awsS4.getS3client();
				}
				if (setDefault.isSelected()) {
					Properties prop = ConfigManager.getProperties();
					if (prop != null) {
						prop.setProperty("access-key", accKey);
						prop.setProperty("secret-access-key", secKey);
						log("Access Keys changed!");
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
				uploadFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				uploadFile.showOpenDialog(frame);

				setUploadFile(uploadFile.getSelectedFile());

			}

			private void setUploadFile(File selectedFile) {
				// TODO Auto-generated method stub
				lblNewLabel.setText(selectedFile.getName());
				uFile = selectedFile;
			}
		});

		dirSelectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser uploadDir = new JFileChooser();
				uploadDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				uploadDir.showOpenDialog(frame);

				setUploadDir(uploadDir.getSelectedFile());
			}

			private void setUploadDir(File selectedFile) {
				// TODO Auto-generated method stub
				lblNewLabel_3.setText(selectedFile.getName());
				uFile = selectedFile;
			}
		});

		uploadBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MainGui.log("Starting Upload...");
				if (fileSelectRadio.isSelected()) {
					uploadSingleFile(keyField.getText(),uFile);
				}else{
					
					uploadDir(keyField.getText(),uFile);
				}

				/*
				 * try { // You can block and wait for the upload to finish
				 * upload.waitForCompletion(); } catch (AmazonClientException
				 * amazonClientException) {
				 * System.out.println("Unable to upload file, upload aborted.");
				 * amazonClientException.printStackTrace(); } catch
				 * (InterruptedException e1) { // TODO Auto-generated catch
				 * block e1.printStackTrace(); }
				 */

			}
		});

		downloadBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String keyTemp = keyList.getSelectedValue();
				if (keyTemp == null) {
					MainGui.log("No selected object to download\nPlease select an object to download!");
				} else {
					final String key = keyTemp.split(" ")[2];

					final JFileChooser downloadDialog = new JFileChooser();

					downloadDialog.setDialogTitle("Set Download Location");
					downloadDialog.showSaveDialog(frame);

					final File dFile = new File("temp");

					MainGui.log("Starting to download " + key);
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							TransferManager tx = new TransferManager(awsS4
									.getCredentials());

							try {

								final Download download = tx.download(
										awsS4.getBucket(), key, dFile);

								download.addProgressListener(new ProgressListener() {

									@Override
									public void progressChanged(
											ProgressEvent progressEvent) {
										// TODO Auto-generated method stub
										System.out.println(download
												.getProgress()
												.getPercentTransferred()
												+ "%");
										MainGui.log(download.getProgress()
												.getPercentTransferred() + "%");

									}

								});

								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											download.waitForCompletion();
											MainGui.log("Download Completed\nDecrypting...");
											EncryptionManager.decrypt(useKeys,
													dFile, downloadDialog
															.getSelectedFile());

										} catch (AmazonServiceException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (AmazonClientException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}

								}).start();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}).start();
				}
			}
		});
	}
	
	public void createFolder(String folderName){
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(awsS4.getBucket(),
					folderName + "/", emptyContent, metadata);
		// send request to S3 to create folder
		awsS4.getS3client().putObject(putObjectRequest);
	}

	public void uploadSingleFile(final String key,final File f) {
		MainGui.log("Uploading "+f.getName());
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				TransferManager tx = new TransferManager(awsS4.getCredentials());
				/*
				 * PutObjectRequest request = new PutObjectRequest(awsS4.
				 * getBucket(),uFile.getName(),uFile);
				 * 
				 * request.setGeneralProgressListener(new ProgressListener() {
				 * 
				 * @Override public void progressChanged(ProgressEvent
				 * progressEvent) { // TODO Auto-generated method stub
				 * MainGui.log("Transferred bytes: " +
				 * progressEvent.getBytesTransferred()); } });
				 */
				KeyGenerator keygen;
				try {
					keygen = KeyGenerator.getInstance("AES");
					keygen.init(128);
					aes_key = keygen.generateKey();
					File enc_File = EncryptionManager.encrypt(useKeys, aes_key,
							f);
					final Upload upload = tx.upload(awsS4.getBucket(),
							key, enc_File);

					upload.addProgressListener(new ProgressListener() {

						@Override
						public void progressChanged(ProgressEvent progressEvent) {
							// TODO Auto-generated method stub
							System.out.println(upload.getProgress()
									.getPercentTransferred() + "%");
							MainGui.log(upload.getProgress()
									.getPercentTransferred() + "%");

						}

					});

					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								upload.waitForCompletion();
								objListing = awsS4.getObjectListing("");
								upDateList();
							} catch (AmazonServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (AmazonClientException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}).start();
	}
	
	public void uploadDir(String key,File f){
		for(File a : f.listFiles()){
			if(a.isDirectory()){
				uploadDir(key+"/"+a.getName(),a);
			}else{
				uploadSingleFile(key + "/"+ a.getName(),a);
			}
		}
	}

	public static void log(String msg) {
		txtrStatus.append(msg + "\n");
	}

	public void upDateList() {
		// TODO Auto-generated method stub
		listModel.removeAllElements();
		for (S3ObjectSummary objectSummary : objListing.getObjectSummaries()) {
			listModel.addElement(" - " + objectSummary.getKey() + "  "
					+ "(size = " + AwS3Conn.findSize(objectSummary.getSize())
					+ " )");
		}
	}

}
