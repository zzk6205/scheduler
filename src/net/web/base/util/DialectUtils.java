package net.web.base.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class DialectUtils {

	private static Logger logger = LoggerFactory.getLogger(DialectUtils.class);

	public static final String DB2 = "db2";

	public static final String ORACLE = "oracle";

	public static final String SQLSERVER = "sqlserver";

	public static final String MYSQL = "mysql";

	private static final DialectUtils instance = new DialectUtils();

	private static Map<DataSource, String> dataSourceMap = new HashMap<DataSource, String>();

	private DialectUtils() {

	}

	public static DialectUtils getInstance() {
		return instance;
	}

	public String getDialect(DataSource dataSource) {
		String dbType = "";
		if (dataSourceMap.containsKey(dataSource)) {
			return dataSourceMap.get(dataSource);
		}
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(dataSource);
			if (conn != null) {
				DatabaseMetaData dbmd = conn.getMetaData();
				if (dbmd != null) {
					String dbName = dbmd.getDatabaseProductName();
					if ((dbName != null) && (dbName.startsWith("DB2/"))) {
						dbType = DB2;
					} else if ((dbName != null) && (dbName.startsWith("Microsoft"))) {
						dbType = SQLSERVER;
					} else if (dbName != null) {
						dbType = dbName.toLowerCase();
					} else {
						logger.error("获取数据库类型失败！");
						throw new RuntimeException("获取数据库类型失败！");
					}
				} else {
					logger.error("无法获取元数据！");
					throw new RuntimeException("无法获取元数据！");
				}
			} else {
				logger.error("无法获取数据库连接！");
				throw new RuntimeException("无法获取数据库连接！");
			}
		} catch (Exception e) {
			logger.error("获取数据库类型失败！", e);
			throw new RuntimeException("获取数据库类型失败！", e);
		} finally {
			if (conn != null) {
				DataSourceUtils.releaseConnection(conn, dataSource);
			}
		}
		if (dbType != null && !"".equals(dbType)) {
			dataSourceMap.put(dataSource, dbType);
		}
		return dbType;
	}

	public String generatePageSql(String sql, DataSource dataSource) {
		String dialect = getDialect(dataSource);
		StringBuilder pageSql = new StringBuilder();
		if (DialectUtils.MYSQL.equals(dialect)) {
			pageSql = buildPageSqlForMysql(sql);
		} else if (DialectUtils.ORACLE.equals(dialect)) {
			pageSql = buildPageSqlForOracle(sql);
		} else if (DialectUtils.DB2.equals(dialect)) {
			pageSql = buildPageSqlForDB2(sql);
		} else if (DialectUtils.SQLSERVER.equals(dialect)) {
			pageSql = buildPageSqlForSQLServer(sql);
		} else {
			return null;
		}
		return pageSql.toString();
	}

	public void generatePageParams(Map<String, Object> params, int pageNumber, int pageSize, DataSource dataSource) {
		String dialect = getDialect(dataSource);
		if (DialectUtils.MYSQL.equals(dialect)) {
			Integer beginIndex = Integer.valueOf((pageNumber - 1) * pageSize);
			params.put("beginIndex", beginIndex);
			params.put("pageSize", pageSize);
		} else if (DialectUtils.ORACLE.equals(dialect)) {
			Integer beginIndex = Integer.valueOf((pageNumber - 1) * pageSize + 1);
			Integer endIndex = Integer.valueOf(pageNumber * pageSize);
			params.put("beginIndex", beginIndex);
			params.put("endIndex", endIndex);
		} else if (DialectUtils.DB2.equals(dialect)) {
			Integer beginIndex = Integer.valueOf((pageNumber - 1) * pageSize + 1);
			Integer endIndex = Integer.valueOf(pageNumber * pageSize);
			params.put("beginIndex", beginIndex);
			params.put("endIndex", endIndex);
		} else if (DialectUtils.SQLSERVER.equals(dialect)) {
			Integer beginIndex = Integer.valueOf((pageNumber - 1) * pageSize + 1);
			Integer endIndex = Integer.valueOf(pageNumber * pageSize);
			params.put("beginIndex", beginIndex);
			params.put("endIndex", endIndex);
		}
	}

	public String generateCountSql(String sql) {
		String countSql = "select count(1) from (" + sql + ") total";
		return countSql;
	}

	public StringBuilder buildPageSqlForMysql(String sql) {
		StringBuilder pageSql = new StringBuilder();
		pageSql.append(sql);
		pageSql.append(" limit :beginIndex, :pageSize");
		return pageSql;
	}

	public StringBuilder buildPageSqlForOracle(String sql) {
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select * from ( select temp.*, rownum row_id from ( ");
		pageSql.append(sql);
		pageSql.append(" ) temp where rownum <= :endIndex ");
		pageSql.append(") a where row_id >= :beginIndex ");
		return pageSql;
	}

	public StringBuilder buildPageSqlForDB2(String sql) {
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select * from ( select temp.*, row_number() over() as row_id from ( ");
		pageSql.append(sql);
		pageSql.append(" ) as temp ) as a ");
		pageSql.append(" where row_id between :beginIndex and :endIndex ");
		return pageSql;
	}

	public StringBuilder buildPageSqlForSQLServer(String sql) {
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select * from ( select temp.*, row_number() over() as row_id from ( ");
		pageSql.append(sql);
		pageSql.append(" ) as temp ) as a ");
		pageSql.append(" where row_id between :beginIndex and :endIndex ");
		return pageSql;
	}

}
