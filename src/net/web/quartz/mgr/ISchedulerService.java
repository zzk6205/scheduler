package net.web.quartz.mgr;

import java.util.List;

import net.web.quartz.entity.ScheduleJob;
import net.web.quartz.entity.ScheduleLog;

public interface ISchedulerService {

	public List<ScheduleJob> getAllScheduleJobList();

	public void insertScheduleLog(ScheduleLog log);

}
