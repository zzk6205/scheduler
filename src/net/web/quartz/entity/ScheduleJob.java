package net.web.quartz.entity;

import net.web.base.entity.BaseEntity;

import org.springframework.util.StringUtils;

public class ScheduleJob extends BaseEntity {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_GROUP_NAME = "#";

	public static final int STATUS_DISABLED = 0;// 禁用
	public static final int STATUS_ENABLED = 1;// 正常

	public static final int ERROR_HANDLE_TERMINATE = 0;// 出错后终止调度
	public static final int ERROR_HANDLE_CONTINUE = 1;// 出错后继续执行

	private String jobId; // 任务编号
	private String jobName; // 任务名称
	private String groupName; // 任务分组名
	private String springBean; // 要调用的SpringBean名
	private String method; // 要调用的SpringBean方法名
	private String description; // 备注
	private String cron; // 时间表达式
	private String params;// 调用参数
	private int status;
	private int errorHandle;// 出错后处理方式

	public String getTriggerName() {
		return this.getJobId() + "_Trigger";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getErrorHandle() {
		return errorHandle;
	}

	public void setErrorHandle(int errorHandle) {
		this.errorHandle = errorHandle;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Object[] getArguments() {
		String[] args = StringUtils.tokenizeToStringArray(this.params, ",; \t\n");
		return new Object[] { args };
	}

}