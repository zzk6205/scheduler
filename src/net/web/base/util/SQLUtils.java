package net.web.base.util;

import org.apache.commons.lang.StringUtils;

public class SQLUtils {

	public static String generateWhere(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return sql;
		}
		String f = sql.trim();
		if (f.startsWith("and ")) {
			f = f.substring(f.indexOf("and ") + 4);
		}
		if (f.startsWith("or ")) {
			f = f.substring(f.indexOf("or ") + 3);
		}
		return " where " + f;
	}

}
