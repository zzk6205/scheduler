package net.web.quartz.service;

import java.util.Map;

import net.web.base.entity.Message;
import net.web.base.entity.Page;
import net.web.quartz.entity.ScheduleJob;

public interface IScheduleJobService {

	public Page getScheduleJobList(Map<String, Object> params, Integer pageNumber, Integer pageSize);

	public Page getScheduleLogList(Map<String, Object> params, Integer pageNumber, Integer pageSize);

	public ScheduleJob getScheduleJob(String jobId);

	public Message insertScheduleJob(ScheduleJob job);

	public Message updateScheduleJob(ScheduleJob job);

	public Message deleteScheduleJob(String jobId);

	public Message startScheduleJob(String jobId);

	public Message stopScheduleJob(String jobId);

}
