package org.hopeframework.utils.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;

/**
 * 所有加解密算法均采用AES128算法，模式为ECB，填充模式为pkcs5（实际就是 pkcs7）
 * 
 * @author DELL
 * 
 */
public class AESUtils {

	private static final String ALGORITHM = "AES";
	private static final String ECBPADDING = "AES/ECB/NoPadding";
	private static final int CACHE_SIZE = 1024;

	/**
	 * 加密数据(key必须是16的倍数)
	 * 
	 * @param content
	 *            内容
	 * @param key
	 *            密钥
	 */
	public static byte[] encrypt(byte[] content, byte[] key) throws Exception {
		if (key == null || key.length != 16) {
//			LogUtils.printLogError("加密密钥长度不是16位");
			return null;
		}
		// 16位数据处理
		content = addByte(content);
		
		SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ECBPADDING);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(content);
		return encrypted;
	}

	/*
	 * 未满16未倍数的数据补全
	 */
	private static byte[] addByte(byte[] content) {
		int mod = content.length % 16;
		if (mod > 0) {
			int len = content.length;
			int addlen = 16-mod;
			byte[] b = new byte[len + addlen];
			for (int i = 0; i < len; i++) {
				b[i] = content[i];
			}
			for (int i = 0; i < addlen; i++) {
				b[len + i] = '0';
			}
			content = b;
		}
		return content;
	}

	/**
	 * 解密数据(content和key必须是16的倍数,对于加密时补长的数据，客户端自行去掉)
	 * 
	 * @param content
	 *            内容
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] key) throws Exception {
		if (key == null || key.length != 16) {
//			LogUtils.printLogError("解密密钥长度不是16位");
			return null;
		}
		if (content == null || (content.length%16)!=0) {
//			LogUtils.printLogError("解密密文长度不是16byte的倍数");
			return null;
		}
		SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ECBPADDING);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		return cipher.doFinal(content);
	}
	
	/**
	 * 文件加密
	 * 
	 * @param key
	 *            密钥
	 * @param sourceFilePath
	 *            源文件
	 * @param destFilePath
	 *            目标文件
	 * @throws Exception
	 */
	public static boolean encryptFile(byte[] key, String sourceFilePath,
			String destFilePath) throws Exception {
		if (key == null || key.length != 16) {
			throw new RuntimeException("解密密钥长度不是16位");
		}

		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);

		if (sourceFile.exists() && sourceFile.isFile()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			destFile.createNewFile();			
			
			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(destFile);
			
			try{
				byte[] buffer = new byte[CACHE_SIZE];
				int nRead = 0;
				while ((nRead = in.read(buffer)) != -1) {
					if(nRead%16>0){
						int n = nRead/16;
						int addlen = 16-nRead%16;
						for(int i=0;i<addlen;i++){
							buffer[nRead+i]='0';
						}
						byte [] tmp = new byte[(n+1)*16];
						System.arraycopy(buffer, 0, tmp, 0, (n+1)*16);
						buffer = tmp;
					}
					out.write(encrypt(buffer, key));
					out.flush();
				}
			}finally{
				out.close();
				in.close();
			}
		}
		return true;
	}
	
	public static byte[] encryptBytes(byte[] key, byte[] source) throws Exception {
		if (key == null || key.length != 16) {
			throw new RuntimeException("解密密钥长度不是16位");
		}
		byte[] output = null;
		if (null != source && source.length > 0) {
			InputStream in = new ByteArrayInputStream(source);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			try{
				byte[] buffer = new byte[CACHE_SIZE];
				int nRead = 0;
				while ((nRead = in.read(buffer)) != -1) {
					if(nRead%16>0){
						int n = nRead/16;
						int addlen = 16-nRead%16;
						for(int i=0;i<addlen;i++){
							buffer[nRead+i]='0';
						}
						byte [] tmp = new byte[(n+1)*16];
						System.arraycopy(buffer, 0, tmp, 0, (n+1)*16);
						buffer = tmp;
					}
					out.write(encrypt(buffer, key));
					out.flush();
				}
				output = out.toByteArray();
			}finally{
				out.close();
				in.close();
			}
		}
		return output;
	}
	/**
	 * 文件解密
	 * @param key 密钥
	 * @param sourceFilePath
	 * @param destFilePath
	 * @param deleteTailLen 要删除尾巴的长度
	 * @return
	 * @throws Exception
	 */
	public static void decryptFile(byte[] key, String sourceFilePath,
			String destFilePath,int deleteTailLen) throws Exception {
		if (key == null || key.length != 16) {
			throw new RuntimeException("解密密钥长度不是16位");
		}
		
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);
		if (sourceFile.exists() && sourceFile.isFile()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			destFile.createNewFile();
			FileInputStream in = new FileInputStream(sourceFile);			
			OutputStream out = new FileOutputStream(destFile);
			try{
				byte[] buffer = new byte[CACHE_SIZE];
				int nRead = 0;
				byte[] cpcache = null;
				int cRead = 0;
				while ((nRead = in.read(buffer)) != -1) {
					if(cRead>0){
						out.write(cpcache,0,cRead);
						out.flush();
					}
					byte[] cache = new byte[nRead];
					System.arraycopy(buffer, 0, cache, 0, nRead);
					cache= decrypt(cache, key);
					cpcache = cache;
					cRead = nRead;
				}
				if(deleteTailLen>0){
					cRead = cRead-deleteTailLen;
				}
				out.write(cpcache,0,cRead);
				out.flush();	
			}finally{
				out.close();			
				in.close();
			}
		}
	}
	
	public static byte[] decryptBytes(byte[] key, byte[] source, int deleteTailLen) throws Exception {
		if (key == null || key.length != 16) {
			throw new RuntimeException("解密密钥长度不是16位");
		}
		byte[] output = null;
		if (null != source && source.length > 0) {
			InputStream in = new ByteArrayInputStream(source);			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try{
				byte[] buffer = new byte[CACHE_SIZE];
				int nRead = 0;
				byte[] cpcache = null;
				int cRead = 0;
				while ((nRead = in.read(buffer)) != -1) {
					if(cRead>0){
						out.write(cpcache,0,cRead);
						out.flush();
					}
					byte[] cache = new byte[nRead];
					System.arraycopy(buffer, 0, cache, 0, nRead);
					cache= decrypt(cache, key);
					cpcache = cache;
					cRead = nRead;
				}
				if(deleteTailLen>0){
					cRead = cRead-deleteTailLen;
				}
				out.write(cpcache,0,cRead);
				out.flush();
				output = out.toByteArray();
			}finally{
				out.close();			
				in.close();
			}
		}
		return output;
	}
}
