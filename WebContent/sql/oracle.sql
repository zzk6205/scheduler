--定时任务
CREATE TABLE PUB_SCHEDULE_JOB (
	JOB_ID        varchar2(36)   NOT NULL, --任务编号
	JOB_NAME      varchar2(30)   NOT NULL, --任务名称
	GROUP_NAME    varchar2(30)   NOT NULL, --分组名称
	SPRING_BEAN	  varchar2(30)   NOT NULL, --spring对应BeanName
	METHOD        varchar2(30)   NOT NULL, --调用方法
	DESCRIPTION   varchar2(100),           --描述
	CRON          varchar2(30)   NOT NULL, --调度表达式
	PARAMS	      varchar2(100),           --参数
	STATUS        number(1,0)    NOT NULL, --状态
	ERROR_HANDLE  number(1,0),             --发现异常后处理方式
	CONSTRAINT PK_PUB_SCHEDULE_JOB PRIMARY KEY ( JOB_ID )
);

--定时任务执行日志
CREATE TABLE PUB_SCHEDULE_LOG (
	LOG_ID          varchar2(36)   NOT NULL, --日志编号
	JOB_ID          varchar2(36)   NOT NULL, --任务编号
	JOB_NAME        varchar2(30)   NOT NULL, --任务名称
	GROUP_NAME      varchar2(30)   NOT NULL, --任务分组
	SPRING_BEAN     varchar2(30)   NOT NULL, --对应spring的BeanName
	METHOD          varchar2(30)   NOT NULL, --调用方法
	CRON            varchar2(30)   NOT NULL, --调度表达式
	PARAMS          varchar2(100),           --参数
	START_DATE      varchar2(19)   NOT NULL, --开始时间
	END_DATE        varchar2(19)   NOT NULL, --结束时间
	SUCCESS         number(1,0)    NOT NULL, --是否正常
	ERROR_TYPE      varchar2(100),           --错误类型
	MSG             varchar2(3000),          --出错信息
	CONSTRAINT PK_PUB_SCHEDULE_LOG PRIMARY KEY ( LOG_ID )
);

insert into PUB_SCHEDULE_JOB
(job_id, job_name, group_name, spring_bean, method, description, cron, params, status, error_handle)
values
('159d73a0-e2e5-4906-b445-7fbf5dbc5d36', 'helloWorld', '#', 'helloWorldQuartzJob', 'execute', null, '*/5 * * * * ?', '1 2 3', 1, 1);