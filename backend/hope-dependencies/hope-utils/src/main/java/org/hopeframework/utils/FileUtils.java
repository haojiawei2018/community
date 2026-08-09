package org.hopeframework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

import org.hopeframework.utils.time.DateUtils;

/**
 * 文件工具
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

	/**
	 * 删除文件
	 *
	 * @param f
	 */
	public static boolean deleteFile(File f) {
		if (f.isFile()) {
			f.delete();
		}
		return true;
	}

	/**
	 * 删除文件
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		return deleteFile(new File(filePath));
	}

	/**
	 * 删除文件夹
	 *
	 * @param dir
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 删除文件夹
	 *
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(String dir) {
		return deleteDir(new File(dir));
	}

	/**
	 * 文件重命名
	 *
	 * @param oldFile
	 * @param newFileName
	 * @return
	 */
	public static boolean renameFile(File oldFile, String newFileName) {
		if (oldFile.isFile()) {
			oldFile.renameTo(new File(
					oldFile.getAbsolutePath().substring(0, oldFile.getAbsolutePath().lastIndexOf(File.separator) + 1)
							+ newFileName));
		}
		return true;
	}

	/**
	 *
	 * @param oldFile
	 * @param newFileName
	 * @return
	 */
	public static boolean renameFile(String oldFile, String newFileName) {
		return renameFile(new File(oldFile), newFileName);
	}

	/**
	 * 将源文件复制一份到目标文件
	 *
	 * @param srcFile
	 *            源文件全路径
	 * @param targetFile
	 *            目标文件全路径
	 * @throws IOException
	 */
	public static void copyFile(String srcFile, String targetFile) throws IOException {
		copyFile(new File(srcFile), new File(targetFile));
	}

	/**
	 *
	 * @param outFilePath
	 */
	public static void mkdirs(String outFilePath) {
		File file = new File(outFilePath);
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * 文件名加后缀
	 * <p>
	 * author: <a href="mailto:shenwei@ancun.com">ShenWei</a><br>
	 *
	 * @param fileName
	 * @param suffix
	 * @return
	 */
	public static String fileNameAddSuffix(String fileName, String suffix) {
		int p = fileName.lastIndexOf(".");
		String fileName1 = fileName.substring(0, p);
		String extName = fileName.substring(p);
		return fileName1 + suffix + extName;
	}

	/**
	 * 将内容写入文件，如果文件不存在，则创建一个新文件
	 *
	 * @param file
	 *            写入的文件物理路径
	 * @param data
	 *            写入的文件内容
	 * @throws IOException
	 */
	public static void writeStringToFile(String file, String data, String webencoding) throws IOException {
		writeStringToFile(new File(file), data, webencoding);

	}

	/**
	 *
	 * @param file
	 * @return 文件内容
	 * @throws IOException
	 */
	public static String readFile(String file) throws IOException {
		return readFileToString(new File(file));

	}

	/**
	 * 创建文件夹 defaultPath/bqbh/userId %100/
	 *
	 * @param defaultPath
	 *            指定存放位置
	 * @param dataId
	 *            用户Id
	 * @return
	 */
	public static String createFolder(String defaultPath, Integer dataId) throws Exception {
		final String separator = System.getProperty("file.separator");
		StringBuffer sb = new StringBuffer(defaultPath);
		sb.append(dataId % 100);
		sb.append(separator);
		sb.append(dataId);
		sb.append(separator);
		sb.append(DateUtils.formatDate("yyMMdd", new Date()));
		sb.append(separator);

		try {
			File myFilePath = new File(sb.toString());
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
			return sb.toString().replace("\\", "/");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 生成uuid
	 *
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 从文件路径获得文件流byte[]
	 *
	 * @param fileName
	 * @return
	 */
	public static byte[] getContent(String fileName) throws IOException {
		File f = new File(fileName);
		if (!f.exists()) {
			throw new FileNotFoundException(fileName);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			CloseUtils.closeStream(in, bos);
		}
	}

	/**
	 * 把文件流转化为文件保存
	 *
	 * @param bfile
	 * @param filePath
	 * @param fileName
	 */
	public static void createFile(byte[] bfile, String filePath, String fileName) throws IOException {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			// 判断文件目录是否存在
			// if (!dir.exists() && dir.isDirectory()) {
			/**
			 * isDirectory():当且仅当此抽象路径名表示的文件存在 且 是一个目录时，返回 true；否则返回 false
			 */
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (IOException e) {
			throw e;
		} finally {
			CloseUtils.closeStream(bos, fos);
		}
	}

	/**
	 * 文件大小转换 B--->M
	 *
	 * @param size
	 * @return
	 */

	public static String fileSizeB2M(String size) {
		String fileSize = "";
		DecimalFormat df = new DecimalFormat("0.00");
		double sizel = Double.valueOf(size).doubleValue();

		fileSize = df.format(sizel / (1024 * 1024));
		return fileSize;
	}
}
