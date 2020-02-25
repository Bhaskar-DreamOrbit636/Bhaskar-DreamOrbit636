package com.reminder.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.Logger;

public class GenerateKey {
	private static Logger logger = Logger.getLogger(GenerateKey.class);

	private static final String public_key = "public.key";
	private static final String private_key = "private.key";

	public static void main(String[] args) {
		// generateKeys();
		generateAES();
	}

	/**
	 * generateKeys
	 */
	private static void generateKeys() {
		logger.info("generateKeys() - start");

		try {
			// Get the public/private key pair
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair keyPair = keyGen.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec rsaPub = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			RSAPrivateKeySpec rsaPrvt = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			GenerateKey test = new GenerateKey();
			test.saveFile(public_key, rsaPub.getModulus(), rsaPub.getPublicExponent());
			test.saveFile(private_key, rsaPrvt.getModulus(), rsaPrvt.getPrivateExponent());

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("generateKeys() - NoSuchAlgorithmException e=" + e, e);

			System.out.println("Exception: " + e);
		}

		logger.info("generateKeys() - end");
	}

	/**
	 * genrate AES key
	 */
	private static void generateAES() {

		SecureRandom srandom = new SecureRandom();
		byte[] iv = new byte[128 / 8];
		srandom.nextBytes(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		String ivFile = "pivfile.iv";
		try (FileOutputStream out = new FileOutputStream(ivFile)) {
			out.write(iv);
		} catch (Exception e) {
			e.printStackTrace();
		}

		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		SecretKey secretKey = keyGen.generateKey();

		String keyFile = "AESKey.key";
		try (FileOutputStream out = new FileOutputStream(keyFile)) {
			byte[] keyb = secretKey.getEncoded();
			out.write(keyb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Key generated");
	}

	/**
	 * File will generate in the current directory
	 * 
	 * @param filename
	 * @param modulus
	 * @param publicExponent
	 */
	private void saveFile(String filename, BigInteger modulus, BigInteger publicExponent) {
		logger.info("saveFile(filename=" + filename + ", modulus=" + modulus + ", publicExponent=" + publicExponent
				+ ") - start");

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filename);
			oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(modulus);
			oos.writeObject(publicExponent);
			System.out.println("file generated");

		} catch (Exception e) {
			logger.error("saveFile(filename=" + filename + ", modulus=" + modulus + ", publicExponent=" + publicExponent
					+ ") - String filename=" + filename + ", BigInteger modulus=" + modulus
					+ ", BigInteger publicExponent=" + publicExponent + ", FileOutputStream fos=" + fos
					+ ", ObjectOutputStream oos=" + oos + ", Exception e=" + e, e);

			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					logger.error("saveFile(filename=" + filename + ", modulus=" + modulus + ", publicExponent="
							+ publicExponent + ") - String filename=" + filename + ", BigInteger modulus=" + modulus
							+ ", BigInteger publicExponent=" + publicExponent + ", FileOutputStream fos=" + fos
							+ ", ObjectOutputStream oos=" + oos + ", IOException e=" + e, e);

					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("saveFile(filename=" + filename + ", modulus=" + modulus + ", publicExponent="
							+ publicExponent + ") - String filename=" + filename + ", BigInteger modulus=" + modulus
							+ ", BigInteger publicExponent=" + publicExponent + ", FileOutputStream fos=" + fos
							+ ", ObjectOutputStream oos=" + oos + ", IOException e=" + e, e);
				}
			}
		}

		logger.info("saveFile(filename=" + filename + ", modulus=" + modulus + ", publicExponent=" + publicExponent
				+ ") - end");
	}
}
