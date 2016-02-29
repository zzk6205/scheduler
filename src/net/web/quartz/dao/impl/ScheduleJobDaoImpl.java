package net.web.quartz.dao.impl;

import java.util.List;
import java.util.Map;

import net.web.base.dao.BaseDaoImpl;
import net.web.base.entity.Page;
import net.web.quartz.dao.IScheduleJobDao;
import net.web.quartz.entity.ScheduleJob;

import org.springframework.stereotype.Repository;

@Repository("scheduleJobDao")
public class ScheduleJobDaoImpl extends BaseDaoImpl implements IScheduleJobDao {

	@Override
	public List<ScheduleJob> getAllScheduleJobList() {
		StringBuffer sb = new StringBuffer();
		sb.append(" select JOB_ID,JOB_NAME,GROUP_NAME,SPRING_BEAN,METHOD,DESCRIPTION,CRON,STATUS,PARAMS,ERROR_HANDLE ");
		sb.append(" from   PUB_SCHEDULE_JOB ");
		return this.queryForCamelCaseObjectList(sb.toString(), ScheduleJob.class);
	}

	@Override
	public Page getScheduleJobList(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select JOB_ID,JOB_NAME,GROUP_NAME,SPRING_BEAN,METHOD,DESCRIPTION,CRON,STATUS,PARAMS,ERROR_HANDLE ");
		sb.append(" from   PUB_SCHEDULE_JOB ");
		if (params.containsKey("jobNameSearch")) {
			sb.append(" where JOB_NAME like '%' || :jobNameSearch || '%' ");
		}
		return this.queryForCamelCaseObjectPage(sb.toString(), params, pageNumber, pageSize, ScheduleJob.class);
	}

	@Override
	public ScheduleJob getScheduleJob(String jobId) {
		ScheduleJob job = new ScheduleJob();
		job.setJobId(jobId);
		StringBuffer sb = new StringBuffer();
		sb.append(" select JOB_ID,JOB_NAME,GROUP_NAME,SPRING_BEAN,METHOD,DESCRIPTION,CRON,STATUS,PARAMS,ERROR_HANDLE ");
		sb.append(" from   PUB_SCHEDULE_JOB ");
		sb.append(" where  JOB_ID = :jobId ");
		return this.queryForCamelCaseObject(sb.toString(), job, ScheduleJob.class);
	}

	@Override
	public int insertScheduleJob(ScheduleJob job) {
		StringBuffer sb = new StringBuffer();
		sb.append(" insert into PUB_SCHEDULE_JOB ");
		sb.append(" (JOB_ID,JOB_NAME,GROUP_NAME,SPRING_BEAN,METHOD,DESCRIPTION,CRON,STATUS,PARAMS,ERROR_HANDLE) ");
		sb.append(" values ");
		sb.append(" (:jobId,:jobName,:groupName,:springBean,:method,:description,:cron,:status,:params,:errorHandle) ");
		return this.update(sb.toString(), job);
	}

	@Override
	public int updateScheduleJob(ScheduleJob job) {
		StringBuffer sb = new StringBuffer();
		sb.append(" update PUB_SCHEDULE_JOB ");
		sb.append(" set    CRON=:cron, DESCRIPTION=:description ");
		sb.append(" where  JOB_ID = :jobId ");
		return this.update(sb.toString(), job);
	}

	@Override
	public int deleteScheduleJob(String jobId) {
		ScheduleJob job = new ScheduleJob();
		job.setJobId(jobId);
		StringBuffer sb = new StringBuffer();
		sb.append(" delete from PUB_SCHEDULE_JOB where JOB_ID=:jobId");
		return this.update(sb.toString(), job);
	}

	@Override
	public int updateScheduleJobStatus(String jobId, int status) {
		ScheduleJob job = new ScheduleJob();
		job.setJobId(jobId);
		job.setStatus(status);
		StringBuffer sb = new StringBuffer();
		sb.append(" update PUB_SCHEDULE_JOB ");
		sb.append(" set    STATUS = :status ");
		sb.append(" where  JOB_ID = :jobId ");
		return this.update(sb.toString(), job);
	}

}
