package utils;

import gui.MainGui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionManager {

	public static KeyPair open_keys(File pub, File priv) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {

		KeyFactory keyfac = KeyFactory.getInstance("RSA");

		

		// Loaded the private key

		FileInputStream privstream = new FileInputStream(priv);
		byte[] privKeybytes = new byte[(int) priv.length()];
		privstream.read(privKeybytes);
		privstream.close();

		
		PrivateKey priv_Key = keyfac.generatePrivate(new PKCS8EncodedKeySpec(privKeybytes));
		
		System.out.println(priv_Key);
		// Loaded the private key
		
		FileInputStream pubstream = new FileInputStream(pub);
		
		byte[] pubKeybytes = new byte[(int) pub.length()];
		pubstream.read(pubKeybytes);
		pubstream.close();

		
		
		PublicKey pub_Key = keyfac.generatePublic(new X509EncodedKeySpec(pubKeybytes));

		java.security.KeyPair keypair = new KeyPair(pub_Key, priv_Key);

		return keypair;
	}
	
	public static void generate_rsa_keys(File pubFile,File privFile ) throws NoSuchAlgorithmException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Entered function:");
		
		/*FileInputStream pubstream = new FileInputStream(pubFile);
		long pub_size = pubFile.length();
		byte[] pubkey_bytes = new byte[(int) pub_size];
		pubstream.read(pubkey_bytes);
		pubstream.close();
		
		FileInputStream privstream = new FileInputStream(privFile);
		long priv_size = privFile.length();
		byte[] privkey_bytes = new byte[(int) priv_size];
		privstream.read(privkey_bytes);
		privstream.close();
		*/
		
		KeyPairGenerator rsaPair = KeyPairGenerator.getInstance("RSA");
		rsaPair.initialize(2048);
		java.security.KeyPair rP = rsaPair.genKeyPair();
		
		byte[] pubkey_bytes = rP.getPublic().getEncoded();
		byte[] privkey_bytes = rP.getPrivate().getEncoded();
		
		//X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubkey_bytes);
		
		
		//PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privkey_bytes);
		
		FileOutputStream pub_key = new FileOutputStream(pubFile);
		pub_key.write(pubkey_bytes);
		pub_key.flush();
		pub_key.close();
		
		FileOutputStream priv_key = new FileOutputStream(privFile);
		priv_key.write(privkey_bytes);
		priv_key.flush();
		priv_key.close();
		
		KeyFactory keyfac = KeyFactory.getInstance("RSA");
		
		PKCS8EncodedKeySpec privkey = new PKCS8EncodedKeySpec(privkey_bytes);
		try {
			PrivateKey privKey = keyfac.generatePrivate(privkey);
		} catch (InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		X509EncodedKeySpec pubkey = new X509EncodedKeySpec(pubkey_bytes);
		try {
			PublicKey pubKey = keyfac.generatePublic(pubkey);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("dsf" + pubkey + " "+ rP.getPublic() + " dsfaewldsfl");
		/*	To print the public and private keys and store them as hex in files
		 */ 
		/*String temp="";
		String pubkey = "";
		System.out.println("Generating public key...");
		float percent = (float) 0.00 ;
		 
		for (int i = 0; i < pubkey_bytes.length; i++) {
			temp = Integer.toString((pubkey_bytes[i] & 0xff) + 0x100, 16) ;
			pubkey += temp.substring(1);
			percent = (float) (((float)i*100.00)/(float)pubkey_bytes.length) ;
			System.out.println(percent);
		}
		
		temp="";
		String privkey = "";
		System.out.println("Generating private key...");
		percent=(float) 0.00;
		for (int i = 0; i < privkey_bytes.length; i++) {
			temp = Integer.toString((privkey_bytes[i] & 0xff) + 0x100, 16);
			privkey += temp.substring(1);
			percent = (float) (((float)i*100.00)/(float)privkey_bytes.length) ;
			System.out.println(percent);
		}
		
		//System.out.println(pubkey);
		PrintWriter pub_key = new PrintWriter(new FileOutputStream(pubFile));
		pub_key.println(pubkey);
		pub_key.close();
		//System.out.println("Writing private key");
		System.out.println(privkey);
		PrintWriter priv_key = new PrintWriter(new FileOutputStream(privFile));
		priv_key.println(privkey);
		priv_key.close();*/
	}
	
	public static File encrypt(KeyPair rP,SecretKey key,File f) throws Exception {
		// TODO Auto-generated method stub
		FileInputStream rawFile = new FileInputStream(f);
		
		File enc_File = new File(f.getName()+ ".tempenc");
		
		long size = f.length();
		long curr = 0;
		Float d;
		FileOutputStream encFile = new FileOutputStream(enc_File);

		Cipher rsa = Cipher.getInstance("RSA");
		rsa.init(Cipher.ENCRYPT_MODE, rP.getPublic());
		byte[] encKey = rsa.doFinal(key.getEncoded());
		System.out.println(encKey.length);

		encFile.write(encKey);

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] input = new byte[64];
		int bytesRead;

		while ((bytesRead = rawFile.read(input)) != -1) {
			byte[] output = cipher.update(input, 0, bytesRead);
			if (output != null) {
				encFile.write(output);
				curr += 64;
				curr=curr<size?curr:size;
				d = (float) ((curr*100.0)/size);
				MainGui.log("Encryption of file "+f.getName()+ " in progress : " + d + "%");
				MainGui.encDec("Encryption of file "+f.getName()+ " in progress... "+Math.round(d)+"%", Math.round(d));
			}
		}

		byte[] output = cipher.doFinal();
		if (output != null) {
			encFile.write(output);
		}
		MainGui.log("Encryption complete!\nUploading...");
		MainGui.encDec("Encryption complete!",100);
		rawFile.close();
		encFile.flush();
		encFile.close();
		
		return enc_File;
	}
	
	public static void decrypt(KeyPair rP, File temp,File dest) throws Exception {
		// TODO Auto-generated method stub
		FileOutputStream decFile = new FileOutputStream(dest);

		FileInputStream encFile = new FileInputStream(temp);

		long size = temp.length();
		long curr = 0;

		Float d;
		Cipher rsa = Cipher.getInstance("RSA");
		rsa.init(Cipher.DECRYPT_MODE, rP.getPrivate());
		byte[] encKey = new byte[256];
		encFile.read(encKey);
		byte[] decKey = rsa.doFinal(encKey);

		SecretKey k = new SecretKeySpec(decKey, 0, decKey.length, "AES");

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, k);

		byte[] in = new byte[64];
		int read;
		while ((read = encFile.read(in)) != -1) {
			byte[] output = cipher.update(in, 0, read);
			if (output != null){
				decFile.write(output);
				curr += 64;
				curr=curr<size?curr:size;
				d = (float)(curr*100.0)/size;
				MainGui.log("Decryption of file "+dest.getName()+ " in progress : " + (double)(curr*100/size) + "%");
				MainGui.encDec("Decryption of file "+dest.getName()+ " in progress... "+Math.round(d)+"%", Math.round(d));
			}
		}

		byte[] output = cipher.doFinal();
		if (output != null)
			decFile.write(output);
		
		MainGui.log("Decryption Complete!");
		MainGui.encDec("Decryption Complete!",100);

		encFile.close();
		temp.delete();
		decFile.flush();
		decFile.close();
		

	}

}
