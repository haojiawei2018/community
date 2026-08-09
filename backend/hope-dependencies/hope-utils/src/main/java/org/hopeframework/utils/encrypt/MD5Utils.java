package org.hopeframework.utils.encrypt;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.hopeframework.utils.CloseUtils;
import org.hopeframework.utils.StringLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * md5工具类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class MD5Utils {
	private static final Logger log = LoggerFactory.getLogger(MD5Utils.class);
	private static final int BUFFERSIZE = 8196;
	private static final String ALGORITHM = "MD5";
	private static final String CHARSET = "UTF-8";

	public MD5Utils() {
	}

	public static String calculateMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hashBytes = md.digest(input.getBytes());

			// Convert the byte array to hexadecimal representation
			StringBuilder hexString = new StringBuilder();
			for (byte hashByte : hashBytes) {
				String hex = Integer.toHexString(0xFF & hashByte);
				if (hex.length() == 1) {
					hexString.append('0');  // Padding for single digit
				}
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String md5(String input) {
		return md5(input, CHARSET);
	}
	/*
	 * 获取MD5加密
     *
			 * @param pwd
     *            需要加密的字符串
     * @return String字符串 加密后的字符串
     */
	public static String MD5fulu(String pwd) {
		try {
			// 创建加密对象
			MessageDigest digest = MessageDigest.getInstance("md5");

			// 调用加密对象的方法，加密的动作已经完成
			byte[] bs = digest.digest(pwd.getBytes());
			// 第一步，将数据全部转换成正数：
			String hexString = "";
			for (byte b : bs) {
				// 第一步，将数据全部转换成正数：
				// 解释：为什么采用b&255
				/*
				 * b:它本来是一个byte类型的数据(1个字节) 255：是一个int类型的数据(4个字节)
				 * byte类型的数据与int类型的数据进行运算，会自动类型提升为int类型 eg: b: 1001 1100(原始数据)
				 * 运算时： b: 0000 0000 0000 0000 0000 0000 1001 1100 255: 0000
				 * 0000 0000 0000 0000 0000 1111 1111 结果：0000 0000 0000 0000
				 * 0000 0000 1001 1100 此时的temp是一个int类型的整数
				 */
				int temp = b & 255;
				// 第二步，将所有的数据转换成16进制的形式
				// 注意：转换的时候注意if正数>=0&&<16，那么如果使用Integer.toHexString()，可能会造成缺少位数
				// 因此，需要对temp进行判断
				if (temp < 16 && temp >= 0) {
					// 手动补上一个“0”
					hexString = hexString + "0" + Integer.toHexString(temp);
				} else {
					hexString = hexString + Integer.toHexString(temp);
				}
			}
			return hexString;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private static String md5(String input, String charsetName) {
		try {
			MessageDigest md5 = MessageDigest.getInstance(ALGORITHM);
			byte md5Bytes[] = md5.digest(input.getBytes(charsetName));
			return StringLocalUtils.byte2hex(md5Bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String md5file(String filename) {
		BufferedInputStream bufferedInputStream = null;
		MessageDigest md;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(filename), BUFFERSIZE);
			md = MessageDigest.getInstance(ALGORITHM);
			byte[] buffer = new byte[BUFFERSIZE];
			int i = 0;
			while ((i = bufferedInputStream.read(buffer)) != -1) {
				md.update(buffer, 0, i);
			}

			return StringLocalUtils.byte2hex(md.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			CloseUtils.closeStream(bufferedInputStream);
		}
	}

	public static String md5reverse(String md5) {
		return "";
	}

	public static String md5(InputStream inputStream) throws Exception {

		BufferedInputStream bufferedInputStream = null;
		MessageDigest md = null;

		try {
			byte[] buffer = new byte[BUFFERSIZE];
			int i = 0;

			bufferedInputStream = new BufferedInputStream(inputStream, BUFFERSIZE);
			md = MessageDigest.getInstance(ALGORITHM);

			while ((i = bufferedInputStream.read(buffer)) != -1) { // >0
				md.update(buffer, 0, i);
			}

			buffer = null;

			byte[] md5Bytes = md.digest();
			return StringLocalUtils.byte2hex(md5Bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				bufferedInputStream = null;
			}
		}
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
			"z" };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		log.debug(" byteLen:" + (null == b ? 0 : b.length));
		for (int i = 0; i < b.length; i++) {
			log.debug("byteToHexString(b[" + i + "]):" + byteToHexString(b[i]));
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	public static String encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			log.debug("resultString.getBytes():" + resultString.getBytes());
			log.debug("md.digest(resultString.getBytes()):" + md.digest(resultString.getBytes()));
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 36;
		int d2 = n % 36;
		int d = d1 + d2;
		if (d >= 36) {
			d = d % 36;
		}
		log.debug("byteToHexString.d:" + d);
		return hexDigits[d];
	}

	public static String generateCommonMD5(String result) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] by = md5.digest(result.getBytes());
			return Hex.encodeHexString(by);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*public static void main(String[] args) {
		System.out.println(md5("aabb"));
	}*/
}
