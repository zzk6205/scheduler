package net.web.quartz.mgr.impl;

import java.util.List;

import javax.annotation.Resource;

import net.web.quartz.dao.IScheduleJobDao;
import net.web.quartz.dao.IScheduleLogDao;
import net.web.quartz.entity.ScheduleJob;
import net.web.quartz.entity.ScheduleLog;
import net.web.quartz.mgr.ISchedulerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("schedulerService")
public class SchedulerServiceImpl implements ISchedulerService {

	@Resource(name = "scheduleJobDao")
	private IScheduleJobDao scheduleJobDao;

	@Resource(name = "scheduleLogDao")
	private IScheduleLogDao scheduleLogDao;

	@Override
	public List<ScheduleJob> getAllScheduleJobList() {
		return scheduleJobDao.getAllScheduleJobList();
	}

	@Override
	@Transactional
	public void insertScheduleLog(ScheduleLog log) {
		scheduleLogDao.insertScheduleLog(log);
	}

}
