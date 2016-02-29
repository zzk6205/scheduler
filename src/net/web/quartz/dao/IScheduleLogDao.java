package net.web.quartz.dao;

import java.util.Map;

import net.web.base.entity.Page;
import net.web.quartz.entity.ScheduleLog;

public interface IScheduleLogDao {

	public Page getScheduleLogList(Map<String, Object> params, Integer pageNumber, Integer pageSize);

	public int insertScheduleLog(ScheduleLog log);

}
