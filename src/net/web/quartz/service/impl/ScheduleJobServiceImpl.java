package net.web.quartz.service.impl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.web.base.entity.Message;
import net.web.base.entity.Page;
import net.web.base.util.SpringContextUtils;
import net.web.quartz.dao.IScheduleJobDao;
import net.web.quartz.dao.IScheduleLogDao;
import net.web.quartz.entity.ScheduleJob;
import net.web.quartz.mgr.ISchedulerManage;
import net.web.quartz.service.IScheduleJobService;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("scheduleJobService")
public class ScheduleJobServiceImpl implements IScheduleJobService {

	private static Logger logger = LoggerFactory.getLogger(ScheduleJobServiceImpl.class);

	@Resource(name = "scheduleJobDao")
	private IScheduleJobDao scheduleJobDao;

	@Resource(name = "scheduleLogDao")
	private IScheduleLogDao scheduleLogDao;

	@Resource(name = "schedulerManage")
	private ISchedulerManage schedulerManage;

	@Override
	public Page getScheduleLogList(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
		return scheduleLogDao.getScheduleLogList(params, pageNumber, pageSize);
	}

	@Override
	public Page getScheduleJobList(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
		return scheduleJobDao.getScheduleJobList(params, pageNumber, pageSize);
	}

	@Override
	public ScheduleJob getScheduleJob(String jobId) {
		return scheduleJobDao.getScheduleJob(jobId);
	}

	@Override
	@Transactional
	public Message insertScheduleJob(ScheduleJob job) {
		// 判断表达式格式
		if (!CronExpression.isValidExpression(job.getCron())) {
			return Message.error("cron表达式格式不正确");
		}

		// 判断bean是否存在
		String beanName = job.getSpringBean();
		if (!SpringContextUtils.containsBean(beanName)) {
			return Message.error("bean不存在");
		}

		// 判断method是否存在
		Object bean = SpringContextUtils.getBean(beanName);
		Class<?>[] args = new Class[1];
		args[0] = String[].class;
		boolean containsMethod = false;
		Method methods[] = bean.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(job.getMethod()) && Arrays.equals(args, method.getParameterTypes())) {
				containsMethod = true;
				break;
			}
		}
		if (!containsMethod) {
			return Message.error("method不存在");
		}

		// 保存
		if (StringUtils.isEmpty(job.getJobId())) {
			job.setJobId(UUID.randomUUID().toString());
		}
		job.setGroupName(ScheduleJob.DEFAULT_GROUP_NAME);
		job.setStatus(ScheduleJob.STATUS_ENABLED);
		scheduleJobDao.insertScheduleJob(job);

		if (ScheduleJob.STATUS_ENABLED == job.getStatus()) {
			try {
				schedulerManage.scheduleJob(job);
			} catch (Exception e) {
				logger.error("任务调度新增异常", job, e);
				return Message.unknown("任务调度未新增成功，请重启应用");
			}
		}
		return Message.success("保存成功");
	}

	@Override
	@Transactional
	public Message updateScheduleJob(ScheduleJob job) {
		scheduleJobDao.updateScheduleJob(job);

		job = scheduleJobDao.getScheduleJob(job.getJobId());
		if (ScheduleJob.STATUS_ENABLED == job.getStatus()) {
			try {
				schedulerManage.scheduleJob(job);
			} catch (Exception e) {
				logger.error("任务调度修改异常", job, e);
				return Message.unknown("任务调度未修改成功，请重启应用");
			}
		}
		return Message.success("保存成功");
	}

	@Override
	@Transactional
	public Message deleteScheduleJob(String jobId) {
		ScheduleJob job = scheduleJobDao.getScheduleJob(jobId);
		scheduleJobDao.deleteScheduleJob(jobId);
		try {
			schedulerManage.deleteJob(job);
		} catch (Exception e) {
			logger.error("任务调度删除异常", job, e);
			return Message.unknown("任务调度未删除成功，请重启应用");
		}
		return Message.success("删除成功");
	}

	@Override
	@Transactional
	public Message startScheduleJob(String jobId) {
		scheduleJobDao.updateScheduleJobStatus(jobId, ScheduleJob.STATUS_ENABLED);

		ScheduleJob job = scheduleJobDao.getScheduleJob(jobId);
		try {
			schedulerManage.scheduleJob(job);
		} catch (Exception e) {
			logger.error("任务调度启用异常", job, e);
			return Message.unknown("任务调度未启用成功，请重启应用");
		}
		return Message.success("启用成功");
	}

	@Override
	@Transactional
	public Message stopScheduleJob(String jobId) {
		scheduleJobDao.updateScheduleJobStatus(jobId, ScheduleJob.STATUS_DISABLED);

		ScheduleJob job = scheduleJobDao.getScheduleJob(jobId);
		try {
			schedulerManage.stopJob(job);
		} catch (Exception e) {
			logger.error("任务调度停用异常", job, e);
			return Message.unknown("任务调度未停用成功，请重启应用");
		}
		return Message.success("停用成功");
	}

}
