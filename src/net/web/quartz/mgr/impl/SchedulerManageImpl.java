package net.web.quartz.mgr.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.web.quartz.entity.ScheduleJob;
import net.web.quartz.ext.MethodInvokingJobExt;
import net.web.quartz.mgr.ISchedulerManage;
import net.web.quartz.mgr.ISchedulerService;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.MethodInvoker;

@Service("schedulerManage")
public class SchedulerManageImpl implements ApplicationContextAware, ISchedulerManage, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(SchedulerManageImpl.class);

	@Resource(name = "schedulerFactory")
	private Scheduler scheduler;

	@Resource(name = "schedulerService")
	private ISchedulerService schedulerService;

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	private String scheduleStart = System.getProperty("schedule.start");

	private boolean isScheduleStart() {
		return "true".equals(scheduleStart);
	}

	public void afterPropertiesSet() throws Exception {
		if (!isScheduleStart())
			return;

		List<ScheduleJob> jobs = this.schedulerService.getAllScheduleJobList();
		for (ScheduleJob job : jobs) {
			if (ScheduleJob.STATUS_ENABLED == job.getStatus()) {
				try {
					this.scheduleJob(job);
				} catch (Exception e) {
					StringWriter strWriter = new StringWriter();
					PrintWriter writer = new PrintWriter(strWriter);
					e.printStackTrace(writer);
					writer.close();
					logger.debug("启动任务\"{}\"过程中发生错误，错误信息:{}", job.getJobName(), strWriter.toString());
				}
			}
		}
	}

	public Date scheduleJob(ScheduleJob scheduleJob) throws Exception {
		if (!isScheduleStart())
			return null;

		if (scheduleJob == null) {
			logger.debug("ignore unknowed scheduleJobId:" + scheduleJob);
			return null;
		}

		Date nextDate;
		// 检测是否已经调度
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getGroupName());
		JobDetailImpl jobDetail = (JobDetailImpl) this.scheduler.getJobDetail(jobKey);
		if (jobDetail == null) {
			// Trigger不存在，就创建一个新的
			MethodInvoker methodInvoker = new MethodInvoker();
			methodInvoker.setTargetObject(this.applicationContext.getBean(scheduleJob.getSpringBean()));
			methodInvoker.setTargetMethod(scheduleJob.getMethod());
			methodInvoker.setArguments(scheduleJob.getArguments());
			methodInvoker.prepare();

			jobDetail = new JobDetailImpl();
			jobDetail.setKey(jobKey);
			jobDetail.setJobClass(MethodInvokingJobExt.class);

			// 记录状态数据
			jobDetail.getJobDataMap().put("methodInvoker", methodInvoker);// 记录方法调用器
			jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);// 记录配置信息
			jobDetail.getJobDataMap().put("schedulerService", this.schedulerService);

			CronTriggerImpl trigger = new CronTriggerImpl();
			trigger.setKey(TriggerKey.triggerKey(scheduleJob.getTriggerName(), scheduleJob.getGroupName()));
			trigger.setCronExpression(scheduleJob.getCron());
			nextDate = this.scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// Job已存在，更新相应的调度设置
			TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getTriggerName(), scheduleJob.getGroupName());
			CronTriggerImpl trigger = (CronTriggerImpl) this.scheduler.getTrigger(triggerKey);
			trigger.setCronExpression(scheduleJob.getCron());
			nextDate = this.scheduler.rescheduleJob(TriggerKey.triggerKey(trigger.getName(), trigger.getGroup()), trigger);
		}

		return nextDate;
	}

	public Date rescheduleJob(ScheduleJob scheduleJob) throws Exception {
		if (!isScheduleStart())
			return null;

		if (scheduleJob == null) {
			logger.debug("ignore unknowed scheduleJobId:" + scheduleJob);
			return null;
		}

		Date nextDate;

		// 检测是否已经调度
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getGroupName());
		JobDetailImpl jobDetail = (JobDetailImpl) this.scheduler.getJobDetail(jobKey);
		if (jobDetail == null) {
			return null;
		} else {
			// Job已存在，更新相应的调度设置
			TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getTriggerName(), scheduleJob.getGroupName());
			CronTriggerImpl trigger = (CronTriggerImpl) this.scheduler.getTrigger(triggerKey);
			trigger.setCronExpression(scheduleJob.getCron());
			nextDate = this.scheduler.rescheduleJob(TriggerKey.triggerKey(trigger.getName(), trigger.getGroup()), trigger);
		}

		return nextDate;
	}

	public void stopJob(ScheduleJob scheduleJob) throws Exception {
		if (!isScheduleStart())
			return;

		if (scheduleJob == null) {
			logger.debug("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 删除调度
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getGroupName());
		JobDetailImpl jobDetail = (JobDetailImpl) this.scheduler.getJobDetail(jobKey);
		if (jobDetail != null) {
			this.scheduler.deleteJob(jobKey);
		}
	}

	public void deleteJob(ScheduleJob scheduleJob) throws Exception {
		if (!isScheduleStart())
			return;

		if (scheduleJob == null) {
			logger.debug("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 删除调度
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getGroupName());
		JobDetailImpl jobDetail = (JobDetailImpl) this.scheduler.getJobDetail(jobKey);
		if (jobDetail != null) {
			this.scheduler.deleteJob(jobKey);
		}
	}

	public void pauseJob(ScheduleJob scheduleJob) throws Exception {
		if (!isScheduleStart())
			return;

		if (scheduleJob == null) {
			logger.debug("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 暂停调度
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getGroupName());
		JobDetailImpl jobDetail = (JobDetailImpl) this.scheduler.getJobDetail(jobKey);
		if (jobDetail != null) {
			this.scheduler.pauseJob(jobKey);
		}
	}

	public void resumeJob(ScheduleJob scheduleJob) throws Exception {
		if (!isScheduleStart())
			return;

		if (scheduleJob == null) {
			logger.debug("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 恢复调度
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getGroupName());
		JobDetailImpl jobDetail = (JobDetailImpl) this.scheduler.getJobDetail(jobKey);
		if (jobDetail != null) {
			this.scheduler.resumeJob(jobKey);
		}
	}

}