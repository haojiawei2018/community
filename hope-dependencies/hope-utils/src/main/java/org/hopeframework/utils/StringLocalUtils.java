package org.hopeframework.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.hopeframework.utils.encrypt.MD5Utils;


import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * 转换工具类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class StringLocalUtils {

	private static SecureRandom secureRand = new SecureRandom();
	private static Random rand = new Random(secureRand.nextLong());
	private static String localhost = getLocalHost();
	private static final List methodList = Arrays.asList("getSection","getConSwitchplaceByHid","getCenterPageListBytid", "getConVehicleByhid");


	public static String getLocalHost() {
		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String byte2hex(byte[] b) {

		String str = "";
		String stmp = "";

		int length = b.length;

		for (int n = 0; n < length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				str += "0";
			}
			str += stmp;
		}

		return str.toLowerCase();
	}

	public static String convertCtrlNAndBlankToHtml(String text) {
		if (StringUtils.isNotBlank(text)) {
			text = text.replace("\n", "<br/>");
		}
		return text;
	}

	public static String getRandom(String string) {

		String str = MD5Utils.md5(
				getUUID() + System.currentTimeMillis() + getRandom(999999999) + rand.nextLong() + localhost + string);
		str = str.toLowerCase();

		return str;
	}

	public static int getRandom(int accuracy) {

		return (int) (Math.random() * accuracy);
	}

	public static long getRandom(long accuracy) {

		return (long) (Math.random() * accuracy);
	}

	public static int getWordLength(String str) {

		str = StringUtils.defaultString(str, "");
		return str.replaceAll("[^\\x00-\\xff]", "**").length();
	}

	public static String nullToStrTrim(String str) {

		if (str == null) {
			str = "";
		}

		return str.trim();
	}

	public static boolean isHalfAngle(String str) {

		str = nullToStrTrim(str);
		return str.length() == getWordLength(str);
	}

	public static int nullToIntZero(String str) {
		if (nullToStrTrim(str).equals("")) {
			str = "0";
		}
		return Integer.valueOf(str, 10);
	}

	public static int getRealLength(String str, String charsetName) {
		if (StringUtils.isBlank(str)) {
			return 0;
		}
		try {
			return str.getBytes(charsetName).length;
		} catch (UnsupportedEncodingException e) {
			return 0;
		}
	}

	/**
	 * 简单检查一下是否是MD5值
	 * 
	 * @param md5
	 * @return
	 */
	public static boolean checkMd5(String md5) {
		if (md5.length() != 32) {
			return false;
		}
		return md5.matches("[0-9A-Fa-f]+");
	}

	public static long nullToLongZero(String str) {

		if (str == null || str.trim().length() == 0) {
			str = "0";
		}

		return Long.valueOf(str.trim(), 10);
	}

	/**
	 * 生成随机验证码
	 * 
	 * @param randstring
	 * @param length
	 * @return
	 */
	public static String getRandom(String randstring, int length) {

		String rand = "";

		Random random = new Random();
		String c = "";
		for (int i = 1; i <= length; i++) {
			c = String.valueOf(randstring.charAt(random.nextInt(randstring.length())));
			rand += c;
		}

		return rand;
	}

	public static double nullToDoubleZero(String str) {
		if (str == null || str.trim().length() == 0) {
			str = "0";
		}
		return Double.valueOf(str.trim()).doubleValue();
	}

	/**
	 * 计算百分比
	 *
	 * @param numerator
	 *            分子
	 * @param denominator
	 *            分母
	 * @return
	 */
	public static String percentage(String numerator, String denominator) {
		if (StringUtils.isEmpty(numerator) || StringUtils.isEmpty(denominator)) {
			return "";
		}

		double percent = nullToDoubleZero(numerator) / nullToDoubleZero(denominator);
		// 获取格式化对象
		NumberFormat nt = NumberFormat.getPercentInstance();
		// 设置百分数精确度2即保留两位小数
		nt.setMinimumFractionDigits(2);

		return nt.format(percent);
	}

	/**
	 * 求平均值
	 * 
	 * @param numerator
	 *            分子
	 * @param denominator
	 *            分母
	 * @return
	 */
	public static String getAvg(String numerator, String denominator) {
		if (StringUtils.isEmpty(numerator) || StringUtils.isEmpty(denominator)) {
			return "";
		}

		double avg = nullToDoubleZero(numerator) / nullToDoubleZero(denominator);
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(avg);
	}

	/**
	 * 检验是是否存在字符串中，分号分隔
	 * 
	 * @param config
	 * @param checkData
	 * @return
	 */
	public static boolean checkConfig(String config, String checkData) {

		return checkData.indexOf(";") == -1 && (";" + config + ";").indexOf(";" + checkData + ";") >= 0;
	}

	public static String encode(String str, String enc) {

		String strEncode = "";

		try {
			if (str != null)
				strEncode = URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return strEncode;
	}

	public static String encodeBase64String(String data, String charsetName) {
		try {
			return Base64.encodeBase64String(data.getBytes(charsetName)).trim();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getUUID() {

		return UUID.randomUUID().toString();
	}

	public static String encodeJson2(Object o) {
		try {
			return encodeJson(o);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String encodeJson(Object o) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 禁止使用int代表Enum的order()來反序列化Enum,非常危險
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

		objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

		//objectMapper.configure(JsonFactory.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 所有日期格式都统一为以下样式
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		return objectMapper/*.defaultPrettyPrintingWriter()*/.writeValueAsString(o);
	}

	public static boolean aroundMethod(String methodName) {
		if (StringUtils.isNotEmpty(methodName)) {
			if (methodList.contains(methodName)) {
				return false;
			}
		}
		return true;
	}
}
