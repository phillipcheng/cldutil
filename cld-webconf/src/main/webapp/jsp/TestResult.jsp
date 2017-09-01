<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.cldutil.util.entity.Logs" %>
<%@ page import="org.cldutil.webconf.InitListener" %>
<%@ page import="org.cldutil.webconf.ConfServlet" %>
<%@ page import="org.cldutil.util.entity.Category" %>
<%@ page import="org.cldutil.util.entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.cldutil.datastore.api.DataStoreManager"%>
<% Logger logger = LogManager.getLogger("cld.jsp");%>
<% String userId="cr"; %>
<% DataStoreManager pm = ConfServlet.cconf.getDefaultDsm();%>
<%
	String taskId = request.getParameter(ConfServlet.REQ_PARAM_TEST_TASKID);
	String command = request.getParameter(ConfServlet.CMD_KEY);
%>
<%
	String status = "Finished";
	//List<Product> prdlist= ConfServlet.cconf.getDefaultDsm().getProductByRootTaskId(taskId);
%>
Task: <%=taskId%>, Status:<%=status%>
<div>
	<!-- <%@include file="prdlist.jspf" %>  -->
</div>




