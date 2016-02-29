<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/jsp/public/header.jsp"%>
<html>
<head>
<title>List</title>
<script type="text/javascript">
$(function() {
	query();
});

function successFormatter(val, row) {
	if (val == "1") {
		return '成功';
	} else if (val == "0") {
		return '<font color="red">失败</font>';
	} else {
		return val;
	}
}

function statusFormatter(val, row) {
	if (val == "0") {
		return '停用';
	} else if (val == "1") {
		return '启用';
	} else {
		return val;
	}
}

function errorHandleFormatter(val, row) {
	if (val == "0") {
		return '出错后终止调度';
	} else if (val == "1") {
		return '出错后继续执行';
	} else {
		return val;
	}
}

function query() {
	$('#jobList').datagrid({
		title : '任务列表',
		method : 'post',
		height : 370,
		singleSelect : true,
		fit : false,
		fitColumns : true,
		striped : true,
		collapsible : true,
		url : "${pageContext.request.contextPath}/scheduleJob/list",
		sortName : 'jobId',
		sortOrder : 'asc',
		remoteSort : false,
		idField : 'jobId',
		pagination : true, // 显示分页
		rownumbers : true, // 显示行号
		queryParams : { jobNameSearch : $("#jobNameSearch").val() },
		columns : [ [ 
						{ field : 'jobId', title : '任务编号', width : 20, sortable : true, halign : 'center', hidden : true }, 
						{ field : 'jobName', title : '任务名称', width : 20, sortable : true, halign : 'center' },
						{ field : 'springBean', title : 'bean name', width : 20, sortable : true, halign : 'center' },
						{ field : 'method', title : 'method', width : 20, sortable : true, halign : 'center' },
						{ field : 'cron', title : 'cron表达式', width : 20, sortable : true, halign : 'center' },
						{ field : 'params', title : '参数', width : 20, sortable : true, halign : 'center' },
						{ field : 'status', title : '状态', width : 20, sortable : true, halign : 'center', formatter : statusFormatter },
						{ field : 'errorHandle', title : '异常处理方式', width : 20, sortable : true, halign : 'center', formatter : errorHandleFormatter },
						{ field : 'description', title : '备注', width : 20, sortable : true, halign : 'center' }
					]
		],
		toolbar : [ {
			text : '新增',
			iconCls : 'icon-add',
			handler : function() {
				forInsert();
			}
		}, {
			text : '修改',
			iconCls : 'icon-edit',
			handler : function() {
				forUpdate();
			}
		}, '-', {
			text : '删除',
			iconCls : 'icon-remove',
			handler : function() {
				forDelete();
			}
		}, '-', {
			text : '启用',
			iconCls : 'icon-ok',
			handler : function() {
				forStart();
			}
		}, '-', {
			text : '停用',
			iconCls : 'icon-cancel',
			handler : function() {
				forStop();
			}
		}, '-', {
			text : '执行情况',
			iconCls : 'icon-tip',
			handler : function() {
				forJobLog();
			}
		}, '-', {
			text : '退出',
			iconCls : 'icon-redo',
			handler : function() {
				forLogout();
			}
		} ],
		onLoadSuccess : function() {
			$('#jobList').datagrid('clearSelections');
		},
		onDblClickRow : function(index, row) {
			forJobLog();
		}
	});
}

function forInsert() {
	$("#editJobForm").find(".easyui-textbox[updateable='false']").textbox({ editable : true });
	$("#editJobForm").find(".easyui-combobox[updateable='false']").combobox({ disabled : false });
	$("#editJobForm").form("reset");
	$("#editJobWin").dialog({
		modal : true,
		collapsible : true,
		resizable : false,
		iconCls : 'icon-edit',
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				if (!$("#editJobForm").form('validate')) {
					return;
				}
				$.ajax({
					url : '${pageContext.request.contextPath}/scheduleJob/insert',
					type : "POST",
					dataType : 'json',
					data : $("#editJobForm").serializeJson(),
					success : function(message) {
						if (message.code == '0000') {
							$.messager.alert("信息提示", message.msg, "info");
							$('#editJobWin').dialog('close');
							$('#jobList').datagrid('reload');
						} else {
							$.messager.alert("信息提示", message.msg, "info");
							if (message.code == '9999') {
								$('#editJobWin').dialog('close');
								$('#jobList').datagrid('reload');
							}
						}
					}
				});
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$('#editJobWin').dialog('close');
			}
		} ]
	});
	$('#editJobWin').dialog('open');
}

function forUpdate() {
	var rows = $('#jobList').datagrid('getSelections');
	if (rows == null || rows == "") {
		$.messager.alert('信息提示', '请选择要修改的任务', 'info');
		return;
	}
	if (rows.length != 1) {
		$.messager.alert('信息提示', '只能选择一条记录', 'info');
		return;
	} else {
		$("#editJobForm").find(".easyui-textbox[updateable='false']").textbox({ editable : false });
		$("#editJobForm").find(".easyui-combobox[updateable='false']").combobox({ disabled : true });
		$.ajax({
			url : '${pageContext.request.contextPath}/scheduleJob/detail',
			type : "POST",
			dataType : 'json',
			data : { jobId : rows[0].jobId },
			success : function(data) {
				var jobId = data.jobId;
				$("#editJobForm").form("reset");
				$("#editJobForm").form("load", data);
				$("#editJobWin").dialog({
					modal : true,
					collapsible : true,
					resizable : false,
					iconCls : 'icon-edit',
					buttons : [ {
						text : '保存',
						iconCls : 'icon-ok',
						handler : function() {
							if (!$("#editJobForm").form('validate')) {
								return;
							}
							var data = $("#editJobForm").serializeJson();
							data.jobId = jobId;
							$.ajax({
								url : '${pageContext.request.contextPath}/scheduleJob/update',
								type : "POST",
								dataType : 'json',
								data : data,
								success : function(message) {
									if (message.code == '0000') {
										$.messager.alert("信息提示", message.msg, "info");
										$('#editJobWin').dialog('close');
										$('#jobList').datagrid('reload');
									} else {
										$.messager.alert("信息提示", message.msg, "info");
										if (message.code == '9999') {
											$('#editJobWin').dialog('close');
											$('#jobList').datagrid('reload');
										}
									}
								}
							});
						}
					}, {
						text : '取消',
						iconCls : 'icon-cancel',
						handler : function() {
							$('#editJobWin').dialog('close');
						}
					} ]
				});
				$('#editJobWin').dialog('open');
			}
		});
	}
}

function forDelete() {
	var rows = $('#jobList').datagrid('getSelections');
	if (rows == null || rows == "") {
		$.messager.alert('信息提示', '请选择要删除的任务', 'info');
		return;
	}
	if (rows.length != 1) {
		$.messager.alert('信息提示', '只能选择一条记录', 'info');
		return;
	} else {
		$.messager.confirm("信息提示", "您确定要删除所选任务吗？", function(r) {
            if (r) {
            	$.ajax({
        			url : '${pageContext.request.contextPath}/scheduleJob/delete',
        			type : "POST",
        			dataType : 'json',
        			data : { jobId : rows[0].jobId },
        			success : function(message) {
        				if (message.code == '0000') {
        					$('#jobList').datagrid('reload');
        					$.messager.alert("信息提示", message.msg, "info");
        				} else {
        					$('#jobList').datagrid('reload');
        					$.messager.alert("信息提示", message.msg, "info");
        				}
        			}
        		});
            }
        });
	}
}

function forStart() {
	var rows = $('#jobList').datagrid('getSelections');
	if (rows == null || rows == "") {
		$.messager.alert('信息提示', '请选择要停用的任务', 'info');
		return;
	}
	if (rows.length != 1) {
		$.messager.alert('信息提示', '只能选择一条记录', 'info');
		return;
	} else {
		if (rows[0].status == "1") {
			$.messager.alert('信息提示', '当前任务已是启用状态', 'info');
			return;	
		}
       	$.ajax({
   			url : '${pageContext.request.contextPath}/scheduleJob/start',
   			type : "POST",
   			dataType : 'json',
   			data : { jobId : rows[0].jobId },
   			success : function(message) {
   				if (message.code == '0000') {
   					$('#jobList').datagrid('reload');
   					$.messager.alert("信息提示", message.msg, "info");
   				} else {
   					$('#jobList').datagrid('reload');
   					$.messager.alert("信息提示", message.msg, "info");
   				}
   			}
   		});
	}
}

function forStop() {
	var rows = $('#jobList').datagrid('getSelections');
	if (rows == null || rows == "") {
		$.messager.alert('信息提示', '请选择要停用的任务', 'info');
		return;
	}
	if (rows.length != 1) {
		$.messager.alert('信息提示', '只能选择一条记录', 'info');
		return;
	} else {
		if (rows[0].status == "0") {
			$.messager.alert('信息提示', '当前任务已是停用状态', 'info');
			return;	
		}
       	$.ajax({
   			url : '${pageContext.request.contextPath}/scheduleJob/stop',
   			type : "POST",
   			dataType : 'json',
   			data : { jobId : rows[0].jobId },
   			success : function(message) {
   				if (message.code == '0000') {
   					$('#jobList').datagrid('reload');
   					$.messager.alert("信息提示", message.msg, "info");
   				} else {
   					$('#jobList').datagrid('reload');
   					$.messager.alert("信息提示", message.msg, "info");
   				}
   			}
   		});
	}
}

function forJobLog() {
	var rows = $('#jobList').datagrid('getSelections');
	if (rows == null || rows == "") {
		$.messager.alert('信息提示', '请选择要查看的任务', 'info');
		return;
	}
	if (rows.length != 1) {
		$.messager.alert('信息提示', '只能选择一条记录', 'info');
		return;
	}
	$("#jobLogWin").dialog({
		modal : true,
		collapsible : true,
		resizable : false,
		iconCls : 'icon-edit',
		onOpen : function() {
			forLoadLog(rows[0].jobId);
		}
	});
	$('#jobLogWin').dialog('open');
}

function forLoadLog(jobId) {
	$('#logList').datagrid({
		method : 'post',
		height : 370,
		singleSelect : true,
		fit : true,
		fitColumns : true,
		striped : true,
		collapsible : true,
		url : "${pageContext.request.contextPath}/scheduleJob/logList",
		remoteSort : false,
		idField : 'logId',
		pagination : true, // 显示分页
		rownumbers : true, // 显示行号
		queryParams : { jobId : jobId },
		columns : [ [ 
						{ field : 'logId', title : '日志编号', width : 20, sortable : true, halign : 'center', hidden : true },
						{ field : 'jobId', title : '任务编号', width : 20, sortable : true, halign : 'center', hidden : true }, 
						{ field : 'jobName', title : '任务名称', width : 20, sortable : true, halign : 'center', hidden : true },
						{ field : 'springBean', title : 'bean name', width : 80, sortable : true, halign : 'center', hidden : true },
						{ field : 'method', title : 'method', width : 80, sortable : true, halign : 'center', hidden : true },
						{ field : 'cron', title : 'cron表达式', width : 90, sortable : true, halign : 'center' },
						{ field : 'params', title : '参数', width : 50, sortable : true, halign : 'center' },
						{ field : 'startDate', title : '开始时间', width : 135, sortable : true, halign : 'center' },
						{ field : 'endDate', title : '结束时间', width : 135, sortable : true, halign : 'center' },
						{ field : 'success', title : '是否成功', width : 80, sortable : true, halign : 'center', formatter : successFormatter }
					]
		],
		onLoadSuccess : function() {
			$('#logList').datagrid('clearSelections');
		},
		onDblClickRow : function(index, row) {
			forLogMsg(row);
		}
	});
}

function forLogMsg(row) {
	if (row.success == '1') {
		return;
	}
	var errorType = row.errorType;
	var msg = row.msg;
	var reg = /[\r\n]/g;
	msg = msg.replace(reg, "<p></p>");
	reg = /[\t]/g;
	msg = msg.replace(reg, "&nbsp;&nbsp;");
	$("#editLogForm").find("#errorType").html(errorType);
	$("#editLogForm").find("#msg").html(msg);
	$("#logMsgWin").dialog({
		modal : true,
		collapsible : true,
		resizable : false,
		iconCls : 'icon-edit'
	});
	$('#logMsgWin').dialog('open');
}

function forLogout() {
	window.location.href = "${pageContext.request.contextPath}/logout";
}
</script>
</head>
<body class="easyui-layout" data-options="fit:true">
	<div data-options="region:'north',title:'查询条件'" style="height:75px;padding:5px;text-align:center">
		<table width="100%" class="editTable">
			<tr>
				<td class="tdTitle" width="10%">任务名称：</td>
				<td class="tdRight" width="20%"><input type="text" id="jobNameSearch" name="jobNameSearch" class="easyui-textbox"></td>
				<td align="left"><a onclick="query()" class="easyui-linkbutton" iconCls="icon-search">查询</a></td>
			</tr>
		</table>
	</div>

	<div data-options="region:'center'">
		<table id="jobList"></table>
	</div>

	<div id="editJobWin" class="easyui-dialog" title="任务编辑" closed="true" style="width:650px;height:199px;">
		<form id="editJobForm">
			<table width="100%" class="editTable">
				<tr>
					<td class="tdTitle">任务名称：</td>
					<td class="tdRight"><input type="text" id="jobName" name="jobName" class="easyui-textbox validatebox" data-options="required:true" updateable="false" /></td>
					<td class="tdTitle">cron表达式：</td>
					<td class="tdRight"><input type="text" id="cron" name="cron" class="easyui-textbox validatebox" data-options="required:true" updateable="true" /></td>
				</tr>
				<tr>
					<td class="tdTitle">bean name：</td>
					<td class="tdRight"><input type="text" id="springBean" name="springBean" class="easyui-textbox validatebox" data-options="required:true" updateable="false" /></td>
					<td class="tdTitle">method：</td>
					<td class="tdRight"><input type="text" id="method" name="method" class="easyui-textbox validatebox" data-options="required:true" updateable="false" /></td>
				</tr>
				<tr>
					<td class="tdTitle">参数：</td>
					<td class="tdRight"><input type="text" id="params" name="params" class="easyui-textbox validatebox" data-options="required:true" updateable="false" /></td>
					<td class="tdTitle">异常处理方式：</td>
					<td class="tdRight">
						<select class="easyui-combobox" id="errorHandle" name="errorHandle" style="width:173px;" updateable="false" editable="false">
							<option value="1">出错后继续执行</option>
							<option value="0">出错后终止调度</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="tdTitle">备注：</td>
					<td class="tdRight" colspan="3"><input type="text" id="description" name="description" class="easyui-textbox" style="width:504px" updateable="true" /></td>
				</tr>
			</table>
		</form>
	</div>

	<div id="jobLogWin" class="easyui-dialog" title="执行日志" closed="true" style="width:700px;height:400px;">
		<table id="logList"></table>
	</div>

	<div id="logMsgWin" class="easyui-dialog" title="异常信息" closed="true" style="width:650px;height:300px;">
		<form id="editLogForm">
			<table width="100%" class="editTable">
				<tr>
					<td class="tdTitle" style="width:65px;">异常类型：</td>
					<td class="tdRight">
						<div id="errorType"></div>
					</td>
				</tr>
				<tr style="height:232px;">
					<td class="tdTitle">异常信息：</td>
					<td class="tdRight">
						<div id="msg"></div>
					</td>
				</tr>
		</table>
		</form>
	</div>
</body>
</html>