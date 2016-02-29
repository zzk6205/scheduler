package net.web.quartz.dao.impl;

import java.util.Map;

import net.web.base.dao.BaseDaoImpl;
import net.web.base.entity.Page;
import net.web.quartz.dao.IScheduleLogDao;
import net.web.quartz.entity.ScheduleLog;

import org.springframework.stereotype.Repository;

@Repository("scheduleLogDao")
public class ScheduleLogDaoImpl extends BaseDaoImpl implements IScheduleLogDao {

	@Override
	public int insertScheduleLog(ScheduleLog log) {
		StringBuffer sb = new StringBuffer();
		sb.append(" insert into PUB_SCHEDULE_LOG ");
		sb.append(" (LOG_ID,JOB_ID,JOB_NAME,GROUP_NAME,SPRING_BEAN,METHOD,CRON,PARAMS,START_DATE,END_DATE,SUCCESS,ERROR_TYPE,MSG) ");
		sb.append(" values ");
		sb.append(" (:logId,:jobId,:jobName,:groupName,:springBean,:method,:cron,:params,:startDate,:endDate,:success,:errorType,:msg) ");
		return this.update(sb.toString(), log);
	}

	@Override
	public Page getScheduleLogList(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select LOG_ID,JOB_ID,JOB_NAME,GROUP_NAME,SPRING_BEAN,METHOD,CRON,PARAMS,START_DATE,END_DATE,SUCCESS,ERROR_TYPE,MSG ");
		sb.append(" from   PUB_SCHEDULE_LOG ");
		sb.append(" where  JOB_ID = :jobId ");
		sb.append(" order by START_DATE desc ");
		return this.queryForCamelCaseObjectPage(sb.toString(), params, pageNumber, pageSize, ScheduleLog.class);
	}

}
