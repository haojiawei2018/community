package org.hopeframework.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipOutputStream;
import org.hopeframework.core.constant.RequestConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * 项目request,response,stream工具类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class WebUtils {

	private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

	private static final String CONTENT_ATTRIBUTE = "javax.servlet.content";
	private static final int BUFFERSIZE = 8196;
	private static final String EMPTY = org.apache.commons.lang3.StringUtils.EMPTY;

	public static void flushBuffer(HttpServletResponse response) {

		try {
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	public static void closeInputStream(InputStream inputStream) {

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			inputStream = null;
		}
	}

	public static void closeOutputStream(OutputStream outputStream) {

		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			outputStream = null;
		}
	}

	public static void closeCipherInputStream(CipherInputStream cipherInputStream) {

		if (cipherInputStream != null) {
			try {
				cipherInputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			cipherInputStream = null;
		}
	}

	public static void closeBufferedInputStream(BufferedInputStream bufferedInputStream) {

		if (bufferedInputStream != null) {
			try {
				bufferedInputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			bufferedInputStream = null;
		}
	}

	public static void closeCipherOutputStream(CipherOutputStream cipherOutputStream) {

		if (cipherOutputStream != null) {
			try {
				cipherOutputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			cipherOutputStream = null;
		}
	}

	public static void closeBufferedOutputStream(BufferedOutputStream bufferedOutputStream) {

		if (bufferedOutputStream != null) {
			try {
				bufferedOutputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			bufferedOutputStream = null;
		}
	}

	public static void closeFileOutputStream(FileOutputStream fileOutputStream) {

		if (fileOutputStream != null) {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			fileOutputStream = null;
		}
	}

	public static void closeFileInputStream(FileInputStream fileInputStream) {

		if (fileInputStream != null) {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			fileInputStream = null;
		}
	}

	public static void closeGZIPInputStream(GZIPInputStream gZIPInputStream) {

		if (gZIPInputStream != null) {
			try {
				gZIPInputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			gZIPInputStream = null;
		}
	}

	public static void closeZipOutputStream(ZipOutputStream zipOutputStream) {

		if (zipOutputStream != null) {
			try {
				zipOutputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			zipOutputStream = null;
		}
	}

	public static void closeWriter(Writer writer) {

		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
			writer = null;
		}
	}

	public static void flush(OutputStream outputStream) {

		try {
			outputStream.flush();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	public static void flush(BufferedOutputStream bufferedOutputStream) {

		try {
			bufferedOutputStream.flush();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	public static void flush(CipherOutputStream cipherOutputStream) {

		try {
			cipherOutputStream.flush();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	public static void flush(FileOutputStream fileOutputStream) {

		try {
			fileOutputStream.flush();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	public static void setNocache(HttpServletResponse response) {

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	public static String getRequestContent(HttpServletRequest request) throws IOException {
		String returnV = null;
		Object o = request.getAttribute(CONTENT_ATTRIBUTE);
		if (o == null) {
			// HttpInputMessage inputMessage = new
			// ServletServerHttpRequest(request);

			StringBuffer sb = new StringBuffer();
			InputStream is = request.getInputStream();

			PushbackInputStream pbis = new PushbackInputStream(is);

			InputStreamReader isr = new InputStreamReader(pbis);
			BufferedReader br = new BufferedReader(isr);
			String s = null;
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			returnV = sb.toString();
			request.setAttribute(CONTENT_ATTRIBUTE, returnV);
		} else {
			returnV = o.toString();
		}
		return returnV;
	}

	public static String getLocalHost() {

		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
		}

		return EMPTY;
	}

	public static String encode(String str, String enc) {

		String strEncode = EMPTY;

		try {
			if (str != null)
				strEncode = URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return strEncode;
	}

	public static String decode(String str, String enc) {
		String result = null;
		if (StringUtils.isEmpty(str)) {
			result = EMPTY;
		} else {
			try {
				result = URLDecoder.decode(str, enc);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String getHeaderDecode(HttpServletRequest request, String headerName, String encoding) {
		String headerValue = request.getHeader(headerName);
		return decode(Optional.fromNullable(headerValue.trim()).or(EMPTY), Optional.fromNullable(encoding).or("UTF-8"));
	}

	public static String generateContent(InputStream inputStream, String encoding) {

		if (inputStream == null) {
			return null;
		}

		BufferedInputStream bufferedInputStream = null;

		StringBuffer content = new StringBuffer();

		try {
			byte[] buffer = new byte[BUFFERSIZE];
			int count = 0;

			bufferedInputStream = new BufferedInputStream(inputStream, BUFFERSIZE);

			while ((count = bufferedInputStream.read(buffer)) != -1) { // >0
				content.append(new String(buffer, 0, count, encoding));
			}

			buffer = null;

			return content.toString();
		} catch (Exception e) {
			return null;
		} finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
				}
				bufferedInputStream = null;
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
				inputStream = null;
			}
		}
	}

	/**
	 * 获取客户端真实ip地址。
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return ip地址
	 */
	public static String getClientIP(HttpServletRequest request) {
		String ip = (String) request.getAttribute(RequestConst.REQUEST_IP);
		if (!StringUtils.isEmpty(ip)) {
			return ip;
		}
		String UNKNOWN = "unknown";
		ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
					ip = request.getRemoteAddr();
				}
			}

			return ip;
		}

		String[] ips = ip.split(",");
		if (ips.length > 1) {
			String tempIP = EMPTY;
			for (int i = 0; i < ips.length; i++) {
				tempIP = Optional.fromNullable(ips[i].trim()).or(EMPTY);
				if (!Strings.isNullOrEmpty(tempIP) && UNKNOWN.equalsIgnoreCase(tempIP)) {
					ip = tempIP;
					break;
				}
			}
		}

		return ip;
	}
}
