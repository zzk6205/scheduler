package net.web.quartz.dao;

import java.util.List;
import java.util.Map;

import net.web.base.entity.Page;
import net.web.quartz.entity.ScheduleJob;

public interface IScheduleJobDao {

	public List<ScheduleJob> getAllScheduleJobList();

	public Page getScheduleJobList(Map<String, Object> params, Integer pageNumber, Integer pageSize);

	public ScheduleJob getScheduleJob(String jobId);

	public int insertScheduleJob(ScheduleJob job);

	public int updateScheduleJob(ScheduleJob job);

	public int deleteScheduleJob(String jobId);

	public int updateScheduleJobStatus(String jobId, int status);

}
