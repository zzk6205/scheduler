<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.security.*"%>
<%
	String error = null;
	String classLocation = null;
	String classLoader = null;
	String className = request.getParameter("classname");
	if ((className != null) && ((className = className.trim()).length() != 0)) {
		try {
			ProtectionDomain pd = Class.forName(className).getProtectionDomain();
			if (pd != null) {
				CodeSource cs = pd.getCodeSource();
				ClassLoader loader = pd.getClassLoader();
				if (cs != null) {
					classLocation = cs.toString();
					classLoader = loader.getClass().toString();
				} else {
					error = "No CodeSource found!";
				}
			} else {
				error = "No ProtectionDomain found!";
			}
		} catch (Throwable t) {
			out.println(t);
		}
	}
%>
<html>
<head>
<title>Servlet Container Class Finder</title>
</head>
<body bgcolor="#d1d7b3">
	<h2>
		<font color="#0000a0">Servlet Container Class Finder</font>
	</h2>
	<p>
		<font color="#000000">
			Enter the fully-qualified name of a Java
			class (e.g. org.apache.oro.text.regex.Perl5Compiler) in the field below.
			The servlet will attempt to load the class and, if successful, query the classes
			<em>java.security.CodeSource</em>
			for the location of the class using the following methods:
			<pre>Class.forName(className).getProtectionDomain().getCodeSource()</pre>
		</font>
	</p>
	<form method="post" action="classfinder.jsp">
		<p>
			Class Name: <input type="text" name="classname" value="" size="40" />
			<input type="submit" value="Submit" />
		</p>
	</form>
	<%
		if ((classLocation != null) || (error != null)) {
			out.println("<table border=\"1\" bgcolor=\"#8080c0\">");
			out.println("\t<caption align=\"top\"><font color=\"#000000\">Search Results</font></caption>");
			out.println("\t<tbody>");
			out.println("\t\t<tr>");
			out.println("\t\t\t<td align=\"right\"><font color=\"#000000\">Class Name:</font></td>");
			out.println("\t\t\t<td><font color=\"#000000\">" + className + "</font></td>");
			out.println("\t\t</tr>");
			out.println("\t\t<tr>");
			if (error != null) {
				out.println("\t\t\t<td align=\"right\"><font color=\"#a00000\">Error:</font></td>");
				out.println("\t\t\t<td><font color=\"#a00000\">" + error + "</font></td>");
			} else {
				out.println("\t\t\t<td align=\"right\"><font color=\"#000000\">Class Location:</font></td>");
				out.println("\t\t\t<td><font color=\"#000000\">" + classLocation + "</font></td></tr><tr>");
				out.println("\t\t\t<td align=\"right\"><font color=\"#000000\">Class Loader:</font></td>");
				out.println("\t\t\t<td><font color=\"#000000\">" + classLoader + "</font></td>");
			}
			out.println("\t\t</tr>");
			out.println("\t</tbody>");
			out.println("</table>");
		}
	%>
</body>
</html>