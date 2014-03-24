package com.app.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 日期的一些帮助方法
 * 
 * @author samoin
 * @since 2011-06-24
 */
public class MyDateUtils {
 
	public static int[] weekArray = { 1, 2, 3, 4, 5, 6, 7 };

	public static String[] weekDescArray = { "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六", "星期日" };

	public static void main(String[] args) {
		System.out.println(getConstellation("1983-10-10"));
	}

	public static String getConstellation(String key) {
		Integer month = Integer.valueOf(key.substring(5, 7));
		Integer day = Integer.valueOf(key.substring(8, 10));
		return getConstellation(month, day);
	}

	public static String getConstellation(Integer month, Integer day) {
		String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
		Integer[] arr = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };
		Integer num = month * 2 - (day < arr[month - 1] ? 2 : 0);
		return s.substring(num, num + 2);
	}

	/** 计算年龄 */
	public static Integer getAge(Date birthDay) {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			return 0;
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		}

		return age;
	}

	/**
	 * 获取时间是周几
	 * 
	 * @param date
	 *            时间
	 * @return 时间在该周内是周几（1-7）
	 */
	public static int getDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		// 从星期日开始，然后是星期一、二……
		int week = c.get(Calendar.DAY_OF_WEEK);
		if (week == 1) {
			week = 7;
		} else {
			week -= 1;
		}
		return week;
	}

	/**
	 * 获取时间为周几的描述
	 * 
	 * @param date
	 *            时间
	 * @return 时间在该周内是周几（星期一 - 星期日）
	 */
	public static String getWeekDesc(Date date) {
		return weekDescArray[getDayOfWeek(date) - 1];
	}

	/**
	 * 获取时间为周几的描述
	 * 
	 * @param date
	 *            时间
	 * @return 时间在该周内是周几（星期一 - 星期日）
	 */
	public static String getWeekDescByIndex(int dayOfWeek) {
		return weekDescArray[dayOfWeek - 1];
	}

	/**
	 * 拼装工作日对应的html串<br/>
	 * 如getWeekHtml(date," <li id="{index}_{date}">commonDay</li> ","<span
	 * class='on'
	 * id="{index}_{date}">curDay</span>","commonDay","curDay","{index
	 * }","{date}","yyyy-MM-dd")<br/>
	 * 可能生成 <li id="1_2011-02-11">星期一</li> <span class='on'
	 * id="2_2011-02-12">星期二</span> <li id="3_2011-02-13">星期三</li>……
	 * 
	 * @param date
	 *            时间
	 * @param commonDayHtml
	 *            普通的文本样式，如 <li id="{index}_{date}">commonDay</li>
	 * @param curDayHtml
	 *            当前工作日的文本样式，如<span class='on' id="{index}_{date}">curDay</span>
	 * @param commonDayReplace
	 *            当前工作日的替换规则串，如commonDay
	 * @param curDayReplace
	 *            当前工作日的替换规则串，如curDay
	 * @param indexReplace
	 *            当前工作日为第几日的替换规则串,如\{index\}(注，{等符号为正则表达式的特殊符号，需要加转义符)
	 * @param dateReplace
	 *            日期的替换规则串,如\{date\}(注，{等符号为正则表达式的特殊符号，需要加转义符)
	 * @param dateFmt
	 *            日期的格式,如 yyyy-MM-dd
	 * @return 拼装的html串
	 */
	public static String getWeekHtml(Date date, String commonDayHtml,
			String curDayHtml, String commonDayReplace, String curDayReplace,
			String indexReplace, String dateReplace, String dateFmt) {
		StringBuffer sb = new StringBuffer();
		int dayOfWeek = getDayOfWeek(date);
		Calendar c = Calendar.getInstance();
		for (int i = 0; i < 7; i++) {
			c.setTime(date);
			if (i == (dayOfWeek - 1)) {
				sb.append(replaceDayHtml(curDayHtml, curDayReplace,
						dayOfWeek - 1, indexReplace, dateReplace, c.getTime(),
						dateFmt));
			} else {
				c.add(Calendar.DATE, -(dayOfWeek - 1 - i));
				sb.append(replaceDayHtml(commonDayHtml, commonDayReplace, i,
						indexReplace, dateReplace, c.getTime(), dateFmt));
			}
		}
		return sb.toString();
	}

	private static String replaceDayHtml(String html, String dayReplace,
			int index, String indexReplace, String dateReplace, Date date,
			String dateFmt) {
		DateFormat df = new SimpleDateFormat(dateFmt);
		return html.replaceAll(dayReplace, getWeekDescByIndex(index + 1))
				.replaceAll(indexReplace, (index + 1) + "")
				.replaceAll(dateReplace, df.format(date));
	}

	/**
	 * 判断当前两个日期相差是否在指定的月份内
	 * 
	 * @param startDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param count
	 *            指定相差的月份
	 * @return true成立，false不成立
	 */
	public static boolean isInCountedMonth(Date startDate, Date endDate,
			int count) {
		if (startDate.getTime() > endDate.getTime()) {
			Date temp;
			temp = startDate;
			startDate = endDate;
			endDate = temp;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, count);
		return calendar.getTime().getTime() >= endDate.getTime();
	}

	/**
	 * 获取指定日期最近指定天数内的日期的集合
	 * 
	 * @param endDate
	 *            指定的日期
	 * @param count
	 *            指定的天数内
	 * @return List<Date>指定天数内的日期的集合
	 */
	public static List<Date> getLastCountDayList(Date endDate, int count) {
		List<Date> result = new LinkedList<Date>();
		Calendar calendar = Calendar.getInstance();
		result.add(endDate);
		boolean flag = true;
		if (count > 1) {
			for (int i = 0; i < count - 1; i++) {
				if (flag) {
					calendar.setTime(endDate);
				}
				calendar.add(Calendar.DATE, -1);
				int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
				// 星期一到星期日是2345671，所以要特殊处理。原理是把周日变成8，然后所有的数减1
				if (weekDay == 1) {
					weekDay = 8;
				}
				weekDay -= 1;
				if (weekDay == 6 || weekDay == 7) {
					calendar.add(Calendar.DATE, -2);
					flag = false;
				}
				result.add(calendar.getTime());
			}
		}
		return result;
	}

	/**
	 * 获取指定日期最近指定天数内的日期的集合
	 * 
	 * @param endDate
	 *            指定的日期
	 * @param count
	 *            指定的天数内
	 * @return Date[]指定天数内的日期的集合
	 */
	public static Date[] getLastCountDayArray(Date endDate, int count) {
		List<Date> list = getLastCountDayList(endDate, count);
		Date[] array = new Date[count];
		return list.toArray(array);
	}

	/**
	 * 转换日期为指定的串
	 * 
	 * @param date
	 *            日期
	 * @param fmt
	 *            格式化的格式
	 * @return 格式化后的日期的串
	 */
	public static String parseDateToStr(Date date, String fmt) {
		DateFormat dateFormat = new SimpleDateFormat(fmt,
				Locale.SIMPLIFIED_CHINESE);
		return dateFormat.format(date);
	}

	public static String parseDateToStr(String fmt) {
		return parseDateToStr(new Date(), fmt);
	}

	/**
	 * 转换日期的串为指定的date类型的日期
	 * 
	 * @param dateStr
	 *            日期的串
	 * @param fmt
	 *            格式化的格式
	 * @return 转换后的日期
	 */
	public static Date parseStrToDate(String dateStr, String fmt) {
		DateFormat dateFormat = new SimpleDateFormat(fmt);
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @param in_date
	 *            输入日期字符串
	 * @param in_format
	 *            输入格式
	 * @param out_format
	 *            输出格式
	 * @return 格式化的日期
	 */
	public static String formatDate(String in_date, String in_format,
			String out_format) {
		return parseDateToStr(parseStrToDate(in_date, in_format), out_format);
	}

	/**
	 * 获取当天的开始时间
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static Date getStartOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取当天的结束时间
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static Date getEndOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 在指定日期上增加指定的小时数
	 * 
	 * @param date
	 *            日期
	 * @param hours
	 *            要增加的小时数
	 * @return
	 */
	public static Date addHour(Date date, int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}

	public static Date addHour(int hours) {
		return addHour(new Date(), hours);
	}

	/**
	 * 在指定日期上增加指定的天数
	 * 
	 * @param date
	 *            日期
	 * @param days
	 *            要增加的天数
	 * @return
	 */
	public static Date addDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	public static Date addDate(int days) {
		return addDate(new Date(), days);
	}

	/**
	 * 在指定日期上增加指定的周数
	 * 
	 * @param date
	 *            日期
	 * @param weeks
	 *            要增加的周数
	 * @return
	 */
	public static Date addWeek(Date date, int weeks) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_MONTH, weeks);
		return calendar.getTime();
	}

	public static Date addWeek(int weeks) {
		return addWeek(new Date(), weeks);
	}

	/**
	 * 在指定日期上增加指定的月数
	 * 
	 * @param date
	 *            日期
	 * @param months
	 *            要增加的月数
	 * @return
	 */
	public static Date addMonth(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	public static Date addMonth(int months) {
		return addMonth(new Date(), months);
	}

	/**
	 * 在指定日期上增加指定的年数
	 * 
	 * @param date
	 *            日期
	 * @param years
	 *            要增加的年数
	 * @return
	 */
	public static Date addYear(Date date, int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, years);
		return calendar.getTime();
	}

	public static Date addYear(int years) {
		return addYear(new Date(), years);
	}

	/**
	 * 获取指定日期当月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}

	/**
	 * 获取日期之前最近的工作日的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getRecentlyWeekDay(Date date) {
		int weekDay = getDayOfWeek(date);
		if (weekDay > 5) {
			date = addDate(date, 5 - weekDay);
		}
		return date;
	}

	// public static void main(String[] args) {
	// Date temp = new Date();
	// temp = getRecentlyWeekDay(temp);
	// System.out.println(MyDateUtils.parseDateToStr(
	// MyDateUtils.getEndOfDate(temp), "yyyy-MM-dd HH:mm:ss"));
	// }
}
