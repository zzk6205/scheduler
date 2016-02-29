package net.web.quartz.ext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.UUID;

import net.web.base.util.DateUtils;
import net.web.base.util.StringUtils;
import net.web.quartz.entity.ScheduleJob;
import net.web.quartz.entity.ScheduleLog;
import net.web.quartz.mgr.ISchedulerService;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean.MethodInvokingJob;
import org.springframework.util.MethodInvoker;

public class MethodInvokingJobExt extends MethodInvokingJob {

	private static Logger logger = LoggerFactory.getLogger(MethodInvokingJobExt.class);

	private MethodInvoker methodInvoker;
	private ScheduleJob scheduleJob;
	private ScheduleLog scheduleLog;
	private ISchedulerService schedulerService;
	private String pattern = "yyyy-MM-dd HH:mm:ss";

	public void setMethodInvoker(MethodInvoker methodInvoker) {
		this.methodInvoker = methodInvoker;
	}

	public void setScheduleJob(ScheduleJob job) {
		this.scheduleJob = job;
	}

	public void setSchedulerService(ISchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	private String buildTime(String startDate, String endDate) {
		long start = (DateUtils.fmtStrToDate(startDate, pattern)).getTime();
		long end = (DateUtils.fmtStrToDate(endDate, pattern)).getTime();
		long w = end - start;
		long h = 0, m = 0, s = 0, ms = 0;
		ms = w % 1000;// 毫秒
		s = w / 1000;// 秒
		m = s / 60;// 分
		s = s % 60;// 秒
		h = m / 60;// 时
		m = m % 60;// 分
		String time = "";
		if (h > 0)
			time += h + "小时";
		if (m > 0)
			time += m + "分";
		if (s > 0)
			time += s + "秒";
		if (ms > 0)
			time += ms + "毫秒";
		return time;
	}

	private void errorLog(JobExecutionContext context, Exception e) {
		scheduleLog.setEndDate(DateUtils.fmtDateToStr(new Date(), pattern));
		scheduleLog.setSuccess(ScheduleLog.EXEC_ERROR);
		scheduleLog.setErrorType(e.getClass().getName());
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		e.printStackTrace(writer);
		writer.close();
		try {
			if (StringUtils.byteLength(strWriter.toString()) > 3000) {
				scheduleLog.setMsg(StringUtils.leftByte(strWriter.toString(), 3000));
			} else {
				scheduleLog.setMsg(strWriter.toString());
			}
		} catch (UnsupportedEncodingException e1) {
			logger.error("任务\"{}\"执行过程发生错误,错误信息:{}-{}", scheduleJob.getJobName(), strWriter.toString(), e1.getMessage());
		}
		this.schedulerService.insertScheduleLog(scheduleLog);
		logger.error("任务\"{}\"执行过程发生错误,请检查任务的错误日志信息", scheduleJob.getJobName());
		if (scheduleJob.getErrorHandle() == ScheduleJob.ERROR_HANDLE_TERMINATE) {
			stopInError(context);
		}
	}

	private void stopInError(JobExecutionContext context) {
		try {
			logger.error("因发生异常,终止任务\"{}\"的后续调度", scheduleJob.getJobName());
			context.getScheduler().unscheduleJob(TriggerKey.triggerKey(scheduleJob.getTriggerName(), scheduleJob.getGroupName()));
			context.getScheduler().deleteJob(JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getGroupName()));
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("正在执行任务\"{}\"({})", scheduleJob.getJobName(), scheduleJob.getDescription());
		scheduleLog = new ScheduleLog();
		scheduleLog.setLogId(UUID.randomUUID().toString());
		scheduleLog.setJobId(scheduleJob.getJobId());
		scheduleLog.setJobName(scheduleJob.getJobName());
		scheduleLog.setGroupName(scheduleJob.getGroupName());
		scheduleLog.setSpringBean(scheduleJob.getSpringBean());
		scheduleLog.setMethod(scheduleJob.getMethod());
		scheduleLog.setCron(scheduleJob.getCron());
		scheduleLog.setParams(scheduleJob.getParams());
		scheduleLog.setStartDate(DateUtils.fmtDateToStr(new Date(), pattern));
		try {
			context.setResult(this.methodInvoker.invoke());
			scheduleLog.setEndDate(DateUtils.fmtDateToStr(new Date(), pattern));
			scheduleLog.setSuccess(ScheduleLog.EXEC_SUCCESS);
			scheduleLog.setErrorType(null);
			scheduleLog.setMsg(null);
			this.schedulerService.insertScheduleLog(scheduleLog);
		} catch (InvocationTargetException ex) {
			errorLog(context, ex);
		} catch (Exception ex) {
			errorLog(context, ex);
		}
		logger.debug("任务\"{}\"执行完毕,耗时{}", scheduleJob.getJobName(), this.buildTime(scheduleLog.getStartDate(), scheduleLog.getEndDate()));
	}

}
