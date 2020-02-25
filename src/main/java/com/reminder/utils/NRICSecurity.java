package com.reminder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/application.properties")
public class NRICSecurity {

	private static Logger logger = Logger.getLogger(NRICSecurity.class);

	//static ResourceBundle mybundle = ResourceBundle.getBundle("application");

/*	@Value("${r365.public.key}")
	private String publickeyFile;

	@Value("${r365.private.key}")
	private String privateKeyFile;
	
	@Value("${r365.AES.key}")
	private String keyFile;
	
	@Value("${r365.IV.key}")
	private String pivFile;*/

	 private String publickeyFile = System.getProperty("r365.public.key");
	 private String privateKeyFile = System.getProperty("r365.private.key");
	 private String keyFile = System.getProperty("r365.AES.key");
	 private String pivFile = System.getProperty("r365.IV.key");

	/**
	 * Encryption of NRIC
	 * 
	 * @param text
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String text) {
		//String publickeyFile = mybundle.getString("r365.private.key");
		byte[] cipherText = text.getBytes();
		byte[] encrypted = null;
		String enc = "";
		try {
			PublicKey publicKey = readPublicKey(publickeyFile);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encrypted = cipher.doFinal(cipherText);
			enc = Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in encryption: " + e);
		}
		return enc;
	}

	@SuppressWarnings("resource")
	private static PublicKey readPublicKey(String filename) {
		PublicKey publicKey = null;
		try (FileInputStream fis = new FileInputStream(new File(filename));
				ObjectInputStream ois = new ObjectInputStream(fis);) {
			BigInteger mod = (BigInteger) ois.readObject();
			BigInteger exponent = (BigInteger) ois.readObject();
			RSAPublicKeySpec rsaPublic = new RSAPublicKeySpec(mod, exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(rsaPublic);
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("Error in Readin gpublic key: " + e);
			e.printStackTrace();
		}
		return publicKey;
	}

	/**
	 * Decryption of NRIC
	 * 
	 * @param text
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String cipherText) {
		//String privateKeyFile = mybundle.getString("r365.private.key");
		byte[] decryptedData = null;
		byte[] cipherByte = Base64.getDecoder().decode(cipherText);
		try {
			PrivateKey privateKey = readPrivateKey(privateKeyFile);
			Cipher decryptChiper = Cipher.getInstance("RSA");
			decryptChiper.init(Cipher.DECRYPT_MODE, privateKey);
			decryptedData = decryptChiper.doFinal(cipherByte);
			return new String(decryptedData);
		} catch (Exception e) {
			logger.error("Error in Decryption: " + e);
		}
		return cipherText;
	}

	@SuppressWarnings("resource")
	private static PrivateKey readPrivateKey(String filename) {
		try (FileInputStream fis = new FileInputStream(new File(filename));
				ObjectInputStream ois = new ObjectInputStream(fis);) {
			BigInteger mod = (BigInteger) ois.readObject();
			BigInteger exponent = (BigInteger) ois.readObject();
			RSAPrivateKeySpec rsaPrivate = new RSAPrivateKeySpec(mod, exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(rsaPrivate);
			return privateKey;
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("Error in Reading private key: " + e);
			e.printStackTrace();
		}
		return null;
	}

	// ------File Encryption and Decryption---------

	private SecretKeySpec readAESKey() {

		//String keyFile = "C:/Reminder365_Local/PSA-365/AESKey.key";
		byte[] keyb = null;
		try {
			keyb = Files.readAllBytes(Paths.get(keyFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
		return skey;

	}

	private IvParameterSpec readIV() {

		//String pivFile = "C:/Reminder365_Local/PSA-365/pivfile.iv";
		byte[] iv = null;
		try {
			iv = Files.readAllBytes(Paths.get(pivFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		return ivspec;
	}

	/**
	 * Encrypt file
	 * 
	 * @param filename
	 * @param outFile
	 */
	public void encrypt(String fileName, String outFile) {
		// String publickeyFile = mybundle.getString("r365.private.key");
		try {
			// PublicKey publicKey = readPublicKey(publickeyFile);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, readAESKey(), readIV());
			processFile(cipher, fileName, outFile);
			DeleteFile(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in Encryption: " + e);
		}
	}

	/**
	 * Decrypt File
	 * 
	 * @param filename
	 * @param outFile
	 */
	public void decrypt(String fileName, String outFile) {
		// String privateKeyFile = mybundle.getString("r365.private.key");
		try {
			// PrivateKey privateKey = readPrivateKey(privateKeyFile);
			Cipher decryptChiper = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decryptChiper.init(Cipher.DECRYPT_MODE, readAESKey(), readIV());
			processFile(decryptChiper, fileName, outFile);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in Decryption: " + e);
		}
	}

	/**
	 * delete file after encryption
	 * 
	 * @param fileName
	 */
	public static void DeleteFile(String fileName) {
		File fil = new File(fileName);
		fil.delete();
	}

	private void processFile(Cipher ci, InputStream in, OutputStream out)
			throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
		byte[] ibuf = new byte[1024];
		int len;
		while ((len = in.read(ibuf)) != -1) {
			byte[] obuf = ci.update(ibuf, 0, len);
			if (obuf != null)
				out.write(obuf);
		}
		byte[] obuf = ci.doFinal();
		if (obuf != null)
			out.write(obuf);
	}

	private void processFile(Cipher ci, String inFile, String outFile)
			throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
		try (FileInputStream in = new FileInputStream(inFile); FileOutputStream out = new FileOutputStream(outFile)) {
			processFile(ci, in, out);
		}
	}
}
