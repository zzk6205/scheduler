package net.web.quartz.controller;

import java.util.Map;

import javax.annotation.Resource;

import net.web.base.entity.Message;
import net.web.base.entity.Page;
import net.web.quartz.entity.ScheduleJob;
import net.web.quartz.service.IScheduleJobService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("ScheduleJobController")
@RequestMapping("/scheduleJob")
public class ScheduleJobController {

	@Resource(name = "scheduleJobService")
	private IScheduleJobService scheduleJobService;

	@RequestMapping(value = "/forList")
	public String forList() {
		return "/schedule/list";
	}

	@RequestMapping(value = "/list")
	@ResponseBody
	public Page list(@RequestParam Map<String, Object> params, Integer page, Integer rows) {
		return scheduleJobService.getScheduleJobList(params, page, rows);
	}

	@RequestMapping(value = "/logList")
	@ResponseBody
	public Page logList(@RequestParam Map<String, Object> params, Integer page, Integer rows) {
		return scheduleJobService.getScheduleLogList(params, page, rows);
	}

	@RequestMapping(value = "/detail")
	@ResponseBody
	public ScheduleJob detail(String jobId) {
		return scheduleJobService.getScheduleJob(jobId);
	}

	@RequestMapping(value = "/insert")
	@ResponseBody
	public Message insert(ScheduleJob job) {
		return scheduleJobService.insertScheduleJob(job);
	}

	@RequestMapping(value = "/update")
	@ResponseBody
	public Message update(ScheduleJob job) {
		return scheduleJobService.updateScheduleJob(job);
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Message delete(String jobId) {
		return scheduleJobService.deleteScheduleJob(jobId);
	}

	@RequestMapping(value = "/start")
	@ResponseBody
	public Message start(String jobId) {
		return scheduleJobService.startScheduleJob(jobId);
	}

	@RequestMapping(value = "/stop")
	@ResponseBody
	public Message stop(String jobId) {
		return scheduleJobService.stopScheduleJob(jobId);
	}

}
