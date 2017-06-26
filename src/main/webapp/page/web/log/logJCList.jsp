<%@ page language="java" import="java.util.*" contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta charset="utf-8">
<title>基础数据日志管理</title>
<meta http-equiv="refresh" content="3600">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/source/js/easyUI/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/source/js/easyUI/themes/icon.css"> 
	<script type="text/javascript" src="${pageContext.request.contextPath}/source/js/easyUI/jquery.easyui.min.js"></script>
	<script src="${pageContext.request.contextPath}/source/js/easyUI/easyui-lang-zh_CN.js"></script>
	<script src="${pageContext.request.contextPath}/source/js/common/validate.js"></script>
	<script src="${pageContext.request.contextPath}/source/js/common/common.js"></script>
<meta name="viewport"
	content="width=device-width, initial-scale=1, minimum-scale=1  ,maximum-scale=1, user-scalable=no" />
<script
	src="${pageContext.request.contextPath}/source/js/pager/jquery.pager.js"></script>
<link
	href="${pageContext.request.contextPath}/source/js/pager/Pager.css"
	rel="stylesheet" />
<script type="text/javascript">
$(document).ready(function(){
	$("#pager").pager({
	    pagenumber:'${SystemLog.pageNo}',                         /* 表示初始页数 */
	    pagecount:'${SystemLog.pageCount}',                      /* 表示总页数 */
	    totalCount:'${SystemLog.totalCount}',				   /* 表示总记录数 */
	    buttonClickCallback:PageClick                     /* 表示点击分页数按钮调用的方法 */                  
	});		
	$("#seaarchNameTemp").keypress(function(e){
		if(e.keyCode == 13){
			search();
		}
	});			
}); 		 
PageClick = function(pageclickednumber) {
	$("#pager").pager({
	    pagenumber:pageclickednumber,                 /* 表示启示页 */
	    pagecount:'${SystemLog.pageCount}',                  /* 表示最大页数pagecount */
	    buttonClickCallback:PageClick                 /* 表示点击页数时的调用的方法就可实现javascript分页功能 */            
	});	
	$("#pageNumber").val(pageclickednumber);          /* 给pageNumber从新赋值 */
	/* 执行Action */
	pagesearch();
}
function search(){
	$("#pageNumber").val("1");
	$("#hid_serarch").val(encodeURI($("#seaarchNameTemp").val()));
	pagesearch(); 
} 
function pagesearch(){
	if ($('#logForm').form('validate')) {
		logForm.submit();
	}
} 
</script>
</head>
<body>
	<div class="con-right" id="conRight">	
		<div class="fl yw-lump">
			<div class="yw-lump-title">
				<i class="yw-icon icon-partner"></i><span>日志列表</span> 
			</div>
		</div>
		<div class="fl yw-lump mt10">
			<form id="logForm" name="logForm"
				action="<%=basePath %>system/log/logJCList.do" method="get">
				<div class=pd10>
					<div class="fl">  
						<span>条件查询：</span>
						<input type="text" id="seaarchNameTemp" validType="SpecialWord" class="easyui-validatebox" placeholder="搜索关键字：内容，名称" value="${SystemLog.searchName}" /> 
						<input type="hidden" name="searchName" id="hid_serarch" /> 
						<span>起止时间:</span>
						<input style="width:129px;height:32px;" name="schBeginTime" value="${Item.schBeginTime }" class="easyui-datebox" data-options="sharedCalendar:'#sj'">
						<span>至</span>
						<input style="width:129px;height:32px;" name="schEndTime" value="${Item.schEndTime }" class="easyui-datebox" data-options="sharedCalendar:'#sj'">								
						<div id="sj" class="easyui-calendar"></div>
						<span class="yw-btn bg-blue ml30 cur" onclick="search();">搜索</span>						
					</div> 
						<div class="cl"></div>				
                     <input type="hidden" id="pageNumber" name="pageNo" value="${SystemLog.pageNo}" />
                     </div>
		     	</form>
		     	</div>
				
           <div class="fl yw-lump"> 
				<table class="yw-cm-table yw-center yw-bg-hover" >
					<tr style="background-color:#D6D3D3;font-weight: bold;"> 				
						<th>日志内容</th>	
						<th>操作类型</th>
						<th>操作时间</th>
						<th>机构</th>		
						<th>操作员</th>
						<th>访问ip</th>			 
					</tr>
					<c:forEach var="item" items="${LogList}">
						<tr>
						<td title="${item.description}">${item.description}</td>
						<td>
							<c:if test="${item.operation ==1}">
								新增
							</c:if> 
							<c:if test="${item.operation ==2}">
								修改
							</c:if> 
							<c:if test="${item.operation ==3}">
								删除
							</c:if>  
							<c:if test="${item.operation ==4}">
								系统操作
							</c:if> 
						</td>
						<td>${item.operTimes}</td> 
						<td>${item.orgName}</td> 
						<td>${item.operName}</td> 
						<td>${item.ip}</td> 
						</tr>
					</c:forEach>
					
				</table>
				<div class="page" id="pager"></div>
				</div>
			</div>
  </body>
</html>

