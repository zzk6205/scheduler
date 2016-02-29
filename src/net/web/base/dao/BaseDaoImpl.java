package net.web.base.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import net.web.base.entity.Page;
import net.web.base.util.DialectUtils;
import net.web.base.util.MapUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class BaseDaoImpl extends NamedParameterJdbcDaoSupport {

	private static Logger logger = LoggerFactory.getLogger(BaseDaoImpl.class);

	@Resource(name = "dataSource")
	protected DataSource dataSource;

	@PostConstruct
	void init() {
		setDataSource(this.dataSource);
	}

	protected Map<String, Object> queryForMap(String sql) {
		return this.getJdbcTemplate().queryForMap(sql);
	}

	protected Map<String, Object> queryForMap(String sql, Object javaBean) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(javaBean);
		return this.getNamedParameterJdbcTemplate().queryForMap(sql, paramSource);
	}

	protected Map<String, Object> queryForMap(String sql, Map<String, ?> params) {
		return this.getNamedParameterJdbcTemplate().queryForMap(sql, params);
	}

	protected <T, V> T queryForCamelCaseObject(String sql, Class<T> clazz) {
		Map<String, Object> map = this.queryForMap(sql);
		try {
			return MapUtils.toCamelCaseObject(clazz, map);
		} catch (Exception e) {
			logger.error("MapUtils.toCamelCaseObject error", e);
			return null;
		}
	}

	protected <T, V> T queryForCamelCaseObject(String sql, Object javaBean, Class<T> clazz) {
		Map<String, Object> map = this.queryForMap(sql, javaBean);
		try {
			return MapUtils.toCamelCaseObject(clazz, map);
		} catch (Exception e) {
			logger.error("MapUtils.toCamelCaseObject error", e);
			return null;
		}
	}

	protected <T, V> T queryForCamelCaseObject(String sql, Map<String, ?> params, Class<T> clazz) {
		Map<String, Object> map = this.queryForMap(sql, params);
		try {
			return MapUtils.toCamelCaseObject(clazz, map);
		} catch (Exception e) {
			logger.error("MapUtils.toCamelCaseObject error", e);
			return null;
		}
	}

	protected List<Map<String, Object>> queryForList(String sql) {
		return this.getJdbcTemplate().queryForList(sql);
	}

	protected List<Map<String, Object>> queryForList(String sql, Object javaBean) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(javaBean);
		return this.getNamedParameterJdbcTemplate().queryForList(sql, paramSource);
	}

	protected List<Map<String, Object>> queryForList(String sql, Map<String, ?> params) {
		return this.getNamedParameterJdbcTemplate().queryForList(sql, params);
	}

	protected <T, V> List<T> queryForCamelCaseObjectList(String sql, Class<T> clazz) {
		List<Map<String, Object>> list = this.queryForList(sql);
		try {
			return MapUtils.toCamelCaseObjectList(clazz, list);
		} catch (Exception e) {
			logger.error("MapUtils.toCamelCaseObjectList error", e);
			return null;
		}
	}

	protected <T, V> List<T> queryForCamelCaseObjectList(String sql, Object javaBean, Class<T> clazz) {
		List<Map<String, Object>> list = this.queryForList(sql, javaBean);
		try {
			return MapUtils.toCamelCaseObjectList(clazz, list);
		} catch (Exception e) {
			logger.error("MapUtils.toCamelCaseObjectList error", e);
			return null;
		}
	}

	protected <T, V> List<T> queryForCamelCaseObjectList(String sql, Map<String, ?> params, Class<T> clazz) {
		List<Map<String, Object>> list = this.queryForList(sql, params);
		try {
			return MapUtils.toCamelCaseObjectList(clazz, list);
		} catch (Exception e) {
			logger.error("MapUtils.toCamelCaseObjectList error", e);
			return null;
		}
	}

	protected int update(String sql) {
		return this.getJdbcTemplate().update(sql);
	}

	protected int update(String sql, Object javaBean) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(javaBean);
		return this.getNamedParameterJdbcTemplate().update(sql, paramSource);
	}

	protected int update(String sql, Map<String, ?> params) {
		return this.getNamedParameterJdbcTemplate().update(sql, params);
	}

	protected int batchUpdate(String sql, List<Object> paramsList) {
		SqlParameterSource[] batchArgs = new SqlParameterSource[paramsList.size()];
		for (int i = 0; i < paramsList.size(); i++) {
			batchArgs[i] = new BeanPropertySqlParameterSource(paramsList.get(i));
		}
		int[] result = this.getNamedParameterJdbcTemplate().batchUpdate(sql, batchArgs);
		int count = 0;
		for (int i : result) {
			if (i == 1) {
				count++;
			}
		}
		return count;
	}

	protected void executeProc(String procName, final Object[] params) {
		StringBuffer sql = new StringBuffer();
		sql.append("{call ");
		sql.append(procName);
		if ((params != null) && (params.length > 0)) {
			sql.append("(");
			for (int i = 0; i < params.length; i++) {
				if (i == params.length - 1) {
					sql.append("?");
				} else {
					sql.append("?,");
				}
			}
			sql.append(")");
		}
		sql.append("}");
		this.getJdbcTemplate().execute(sql.toString(), new CallableStatementCallback<Object>() {
			@Override
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				if ((params != null) && (params.length > 0)) {
					for (int i = 1; i <= params.length; i++) {
						cs.setObject(i, params[(i - 1)]);
					}
				}
				cs.execute();
				return null;
			}
		});
	}

	protected Page queryForPage(String sql, int pageNumber, int pageSize) {
		DialectUtils dialectUtils = DialectUtils.getInstance();
		Page page = new Page();
		page.setPageSize(pageSize);
		page.setPageNumber(pageNumber);

		String countSQL = dialectUtils.generateCountSql(sql);
		String pageSQL = dialectUtils.generatePageSql(sql, getDataSource());

		int recordSum = 0;
		List<Map<String, Object>> countList = this.queryForList(countSQL);
		if ((countList != null) && (countList.size() == 1)) {
			Map<String, Object> map = (Map<String, Object>) countList.get(0);
			recordSum = ((BigDecimal) map.get("count(1)")).intValue();
		}
		page.setTotal(recordSum);

		Map<String, Object> params = new HashMap<String, Object>();
		dialectUtils.generatePageParams(params, pageNumber, pageSize, getDataSource());
		List<Map<String, Object>> pageList = this.queryForList(pageSQL, params);
		page.setRows(pageList);

		return page;
	}

	protected Page queryForPage(String sql, Map<String, Object> params, int pageNumber, int pageSize) {
		DialectUtils dialectUtils = DialectUtils.getInstance();
		Page page = new Page();
		page.setPageSize(pageSize);
		page.setPageNumber(pageNumber);

		String countSQL = dialectUtils.generateCountSql(sql);
		String pageSQL = dialectUtils.generatePageSql(sql, getDataSource());

		int recordSum = 0;
		List<Map<String, Object>> countList = this.queryForList(countSQL, params);
		if ((countList != null) && (countList.size() == 1)) {
			Map<String, Object> map = (Map<String, Object>) countList.get(0);
			recordSum = ((BigDecimal) map.get("count(1)")).intValue();
		}
		page.setTotal(recordSum);

		dialectUtils.generatePageParams(params, pageNumber, pageSize, getDataSource());
		List<Map<String, Object>> pageList = this.queryForList(pageSQL, params);
		page.setRows(pageList);

		return page;
	}

	protected <T, V> Page queryForCamelCaseObjectPage(String sql, int pageNumber, int pageSize, Class<T> clazz) {
		DialectUtils dialectUtils = DialectUtils.getInstance();
		Page page = new Page();
		page.setPageSize(pageSize);
		page.setPageNumber(pageNumber);

		String countSQL = dialectUtils.generateCountSql(sql);
		String pageSQL = dialectUtils.generatePageSql(sql, getDataSource());

		int recordSum = 0;
		List<Map<String, Object>> countList = this.queryForList(countSQL);
		if ((countList != null) && (countList.size() == 1)) {
			Map<String, Object> map = (Map<String, Object>) countList.get(0);
			recordSum = ((BigDecimal) map.get("count(1)")).intValue();
		}
		page.setTotal(recordSum);

		Map<String, Object> params = new HashMap<String, Object>();
		dialectUtils.generatePageParams(params, pageNumber, pageSize, getDataSource());
		List<T> pageList = this.queryForCamelCaseObjectList(pageSQL, params, clazz);
		page.setRows(pageList);

		return page;
	}

	protected <T, V> Page queryForCamelCaseObjectPage(String sql, Map<String, Object> params, int pageNumber, int pageSize, Class<T> clazz) {
		DialectUtils dialectUtils = DialectUtils.getInstance();
		Page page = new Page();
		page.setPageSize(pageSize);
		page.setPageNumber(pageNumber);

		String countSQL = dialectUtils.generateCountSql(sql);
		String pageSQL = dialectUtils.generatePageSql(sql, getDataSource());

		int recordSum = 0;
		List<Map<String, Object>> countList = this.queryForList(countSQL, params);
		if ((countList != null) && (countList.size() == 1)) {
			Map<String, Object> map = (Map<String, Object>) countList.get(0);
			recordSum = ((BigDecimal) map.get("count(1)")).intValue();
		}
		page.setTotal(recordSum);

		dialectUtils.generatePageParams(params, pageNumber, pageSize, getDataSource());
		List<T> pageList = this.queryForCamelCaseObjectList(pageSQL, params, clazz);
		page.setRows(pageList);

		return page;
	}

}
