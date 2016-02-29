package net.web.quartz.mgr;

import java.util.Date;

import net.web.quartz.entity.ScheduleJob;

public interface ISchedulerManage {

	public Date scheduleJob(ScheduleJob scheduleJob) throws Exception;

	public Date rescheduleJob(ScheduleJob scheduleJob) throws Exception;

	public void stopJob(ScheduleJob scheduleJob) throws Exception;

	public void deleteJob(ScheduleJob scheduleJob) throws Exception;

	public void pauseJob(ScheduleJob scheduleJob) throws Exception;

	public void resumeJob(ScheduleJob scheduleJob) throws Exception;

}
