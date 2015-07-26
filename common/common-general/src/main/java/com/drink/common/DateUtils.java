package com.drink.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateUtils {
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final long DAY_LENGTH_MILLS = 86400000;

	public static Date parseString2Date(String date, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(date);
	}

	public static boolean checkDate(String date) {
		String regex = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(date);
		return matcher.matches();
	}

	public static int betweenDays(Date start, Date end) {
		long startTime = start.getTime();
		long endTime = end.getTime();
		long betweenDays = (endTime - startTime) / DAY_LENGTH_MILLS;
		return (int) betweenDays + 1;
	}

	public static int[] getDateInfo(String str) {
		Date date;
		try {
			date = parseString2Date(str, DATE_FORMAT);
		} catch (ParseException e) {
			throw new RuntimeException();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int season = (month % 3 == 0 ? month / 3 : month / 3 + 1);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int[] arr = { year, season, month, week, day };
		return arr;
	}

	public static String getFirstDayOfThisWeek(String date, String format) throws ParseException {
		Date d = parseString2Date(date, format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.DAY_OF_WEEK, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return parseDate2Str(cal, format);
	}

	public static String getFirstDayOfThisMonth(String date, String format) throws ParseException {
		Date d = parseString2Date(date, format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return parseDate2Str(cal, format);
	}

	public static String parseDate2Str(Calendar calendar, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dateStr = sdf.format(calendar.getTime());
		return dateStr;
	}

	public static String getLastDayOfThisMonth(String date, String format) throws ParseException {
		Date d = parseString2Date(date, format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MONDAY, 1);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return parseDate2Str(cal, format);
	}

	public static String getLastDayOfThisWeek(String date, String format) throws ParseException {
		Date d = parseString2Date(date, format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.WEEK_OF_YEAR, 0);
		cal.set(Calendar.DAY_OF_WEEK, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return parseDate2Str(cal, format);
	}

	public static long toDays(Calendar date) {
		if (null != date) {
			return toDays(date.getTime());
		} else {
			throw new NullPointerException();
		}
	}

	public static long toDays(Date date) {
		if (null != date) {
			return date.getTime() / DAY_LENGTH_MILLS;
		} else {
			throw new NullPointerException();
		}
	}

	// Test
	public static void main(String[] args) {
		System.out.println(toDays(Calendar.getInstance()));
		Calendar another = Calendar.getInstance();
		another.set(Calendar.DATE, 5);
		System.out.println(toDays(another));
		// 1970年1月1日
		another.set(1970, 0, 1);
		System.out.println(toDays(another));
	}

	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}
}