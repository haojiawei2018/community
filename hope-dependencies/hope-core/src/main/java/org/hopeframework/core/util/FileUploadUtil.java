package org.hopeframework.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.utils.FileUtils;
import org.hopeframework.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 文件上传工具类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class FileUploadUtil {

	public static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);

	/**
	 * request中读取内容
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static InputStream readFromRequest(HttpServletRequest request) throws IOException {
		InputStream inputStream = null;
		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(request)) {
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				// 一次遍历所有文件
				MultipartFile file = multiRequest.getFile(iter.next().toString());
				if (file != null) {
					inputStream = file.getInputStream();
					break;
				}
			}
		} else {
			inputStream = request.getInputStream();
		}
		return inputStream;
	}

	/**
	 * 字节流写入文件
	 *
	 * @param inputStream
	 * @param tempFile
	 *            文件完整路径
	 */
	public static void write2File(InputStream inputStream, String tempFile) {
		OutputStream outputStream = null;
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		int BUFFERSIZE = 8196;
		MessageDigest plaintext = null;

		int code = 0;
		try {
			// 判断文件是否存在
			if (inputStream == null) {
				throw new HopeException(ResponseConst.UPLOAD_FILE_IS_NOT_EXISTS);
			}

			plaintext = MessageDigest.getInstance("MD5");
			// 生成文件
			File file = new File(tempFile);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			// 写文件与签名
			byte[] buffer = new byte[BUFFERSIZE];
			int count = 0;
			// 写文件与签名
			outputStream = new FileOutputStream(file);
			bufferedInputStream = new BufferedInputStream(inputStream, BUFFERSIZE);
			bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFERSIZE);

			while ((count = bufferedInputStream.read(buffer)) != -1) { // >0
				bufferedOutputStream.write(buffer, 0, count);
				plaintext.update(buffer, 0, count);
			}
			buffer = null;
			StreamUtils.flush(bufferedOutputStream);

		} catch (Exception e) {
			//logger.info("写文件失败,文件名称 ={}", tempFile);
			// 删除临时文件
			FileUtils.deleteFile(tempFile);
			throw new HopeException(ResponseConst.UPLOAD_FILE_PROCESS_EXCEPTION, null, e);
		} finally {
			StreamUtils.closeInputStream(inputStream);
			StreamUtils.closeBufferedInputStream(bufferedInputStream);
			StreamUtils.closeOutputStream(outputStream);
			StreamUtils.closeBufferedOutputStream(bufferedOutputStream);
		}
	}
}
