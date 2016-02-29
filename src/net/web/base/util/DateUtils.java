package net.web.base.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateUtils {

	public static String fmtDateToStr(Date date, String pattern) {
		if (date == null)
			return "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static Date fmtStrToDate(String str, String pattern) {
		if (StringUtils.isEmpty(str))
			return null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.parse(str);
		} catch (Exception e) {
			return null;
		}
	}

}
