<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>错误页面</title>
</head>
<body>
	<div>
		<pre>
        <%
        	try {
        		//全部内容先写到内存，然后分别从两个输出流再输出到页面和文件
        		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        		PrintStream printStream = new PrintStream(byteArrayOutputStream);

        		printStream.println();
        		printStream.println("尊敬的用户: ");
        		printStream.println("系统出现了异常，请重试。");
        		printStream.println("如果问题重复出现，请向系统管理员反馈。");
        		printStream.println("详细错误信息如下：");
        		printStream.println();

        		printStream.println("访问的路径: " + request.getAttribute("javax.servlet.forward.request_uri"));
        		printStream.println();

        		printStream.println("异常信息");
        		printStream.println(exception.getClass() + " : " + exception.getMessage());
        		printStream.println();

        		Enumeration<String> e = request.getParameterNames();
        		if (e.hasMoreElements()) {
        			printStream.println("请求中的Parameter包括：");
        			while (e.hasMoreElements()) {
        				String key = e.nextElement();
        				printStream.println(key + "=" + request.getParameter(key));
        			}
        			printStream.println();
        		}

        		printStream.println("堆栈信息");
        		exception.printStackTrace(printStream);
        		printStream.println();

        		out.print(byteArrayOutputStream);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        %>
		</pre>
	</div>
</body>
</html>