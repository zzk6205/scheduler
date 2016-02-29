package net.web.quartz.entity;

import net.web.base.entity.BaseEntity;

public class ScheduleLog extends BaseEntity {

	private static final long serialVersionUID = 1L;

	public static final int EXEC_ERROR = 0;// 执行失败
	public static final int EXEC_SUCCESS = 1;// 执行成功

	private String logId;
	private String jobId;
	private String jobName;
	private String groupName;
	private String springBean;
	private String method;
	private String cron;
	private String params;// 调用参数
	private String startDate; // 任务的启动时间
	private String endDate; // 任务的结束时间
	private String errorType; // 异常分类
	private String msg; // 任务信息：如果任务运行异常则为异常信息
	private int success; // 任务是否处理成功

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSpringBean() {
		return springBean;
	}

	public void setSpringBean(String springBean) {
		this.springBean = springBean;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

}
