package org.hopeframework.utils.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

/**
 * 日期工具
 * 
 * @author <a href="mailto:shenwei@ancun.com">ShenWei</a>
 * @since
 */
public class DateUtils {

	/**
	 * 获取当前Date
	 * 
	 * @return date
	 */
	public static Date getCurrentDate() {
		Date today = new Date();
		return today;
	}

	/**
	 * 获取当前Calendar
	 * 
	 * @return 当前Calendar
	 */
	public static Calendar getCurrentCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar;
	}

	/**
	 * 
	 * @param date
	 * @return Calendar
	 */
	public static Calendar getCalendar(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal;
	}

	/**
	 * 
	 * <p>
	 * author: <a href="mailto:shenwei@ancun.com">ShenWei</a><br>
	 * 
	 * @param formatString
	 * @param strDate
	 */
	public static Date convertStringToDate(String formatString, String strDate) {
		if (StringUtils.isBlank(strDate)) {
			throw new IllegalArgumentException("arg strDate[" + strDate + "] format is wrong");
		}
		if (StringUtils.isBlank(formatString)) {
			throw new IllegalArgumentException("arg formatString[" + formatString + "] format is wrong");
		}

		SimpleDateFormat df = new SimpleDateFormat(formatString);
		Date date = null;

		try {
			date = df.parse(strDate);
		} catch (ParseException ex) {
			throw new IllegalArgumentException("arg formatString[" + formatString + "] format is wrong", ex);
		}

		return (date);
	}

	/**
	 * 
	 * <p>
	 * author: <a href="mailto:shenwei@ancun.com">ShenWei</a><br>
	 * 
	 * @param formatString
	 * @param date
	 */
	public static String formatDate(String formatString, Date date) {
		SimpleDateFormat df = null;
		String returnValue = null;

		if (date != null) {
			df = new SimpleDateFormat(formatString);
			returnValue = df.format(date);
		}

		return returnValue;
	}
	
	/**
	 * 根据当前时间得到一个月前的时间 只包括 年月日
	* @return
	* <p>
	* author: xuyuanyang<br>
	 */
	public static String getDateBefore1M(){
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH)+1;
		int date = now.get(Calendar.DAY_OF_MONTH);
		int[] smallMonths = {2,4,6,9,11};
		if(month-1<=0){ //跨年
			year = year -1;
			month = 12;
		}
		if((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0) && month==3){ //如果1月前是闰年的2月，那么该月的最大天数是28
			month=2;
			date = date>28 ? 28 : date;
		}else if(Arrays.asList(smallMonths).contains(month-1)){//如果1月前是小月，那么最大的天数是30
			month=month-1;
			date = date>30 ? 30 : date;
		}else{
			month = month-1;
		}
		String months = String.valueOf(month).length()==1 ? "0"+month : ""+ month;
		String dates = String.valueOf(date).length()==1 ? "0"+date : ""+ date; 
		return year+"-"+months+"-"+dates;
	}
	
	
	/**
	 * 获取日期
	 * 
	 * @param date
	 *            Date
	 * @return int
	 */
	public static final int getDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}
	
	/**
	 * 计算两个日期的月份
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getMonthsBetweenDates(Date startDate, Date endDate)
	{
		if(startDate.getTime() > endDate.getTime())
		{
			Date temp = startDate;
			startDate = endDate;
			endDate = temp;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);

		int yearDiff = endCalendar.get(Calendar.YEAR)- startCalendar.get(Calendar.YEAR);
		int monthsBetween = endCalendar.get(Calendar.MONTH)-startCalendar.get(Calendar.MONTH) +12*yearDiff;

		if(endCalendar.get(Calendar.DAY_OF_MONTH) >= startCalendar.get(Calendar.DAY_OF_MONTH))
			monthsBetween = monthsBetween + 1;
		return monthsBetween;

	}
	
	public static int getIntervalDays(Date firstDate, Date lastDate) {
		if (null == firstDate || null == lastDate) {
			return -1;
		}

		long intervalMilli = lastDate.getTime() - firstDate.getTime();
		return (int) (intervalMilli / (24 * 60 * 60 * 1000));
	}

	/**
	 * 两个时间间隔除以24小时
	 * 
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public static int daysOfTwo(Date firstDate, Date lastDate) {
		return getIntervalDays(firstDate, lastDate);
	}
	
	/**
	 * 增加月数
	 * 
	 * @param date
	 * @param month
	 *            需要增加的月数，比如需要增加2个月，就传入2
	 * @return
	 */

	public static Date addMonth(Date date, int month) {
		if (null == date) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (month != 0) {
			calendar.add(Calendar.MONTH, month);
		}
		return calendar.getTime();
	}
}
