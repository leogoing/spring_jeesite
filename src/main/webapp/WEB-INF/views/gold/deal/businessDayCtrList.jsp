<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>营业日管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/gold/businessDayCtr/list">营业日管理列表</a></li>
		<shiro:hasPermission name="gold:businessDayCtr:create">
			<li ><a href="${ctx}/gold/businessDayCtr/preAdd">营业日管理添加</a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="gold:businessDayDetail:view">
			<li ><a href="${ctx}/gold/businessDayCtr/historyList">营业日历史</a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="businessDayCtr" action="${ctx}/gold/businessDayCtr/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>正则匹配：</label>
				<form:input path="businessRegex" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<c:if test="true"><li><label>状态：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('gold_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li></c:if>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>营业日正则匹配式</th>
				<th>开始营业时间</th>
				<th>关账时间</th>
				<th>优先级</th>
				<th>状态</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="businessDayCtr">
			<tr>
				<td><a href="${ctx}/gold/businessDayCtr/detail?id=${businessDayCtr.id}">
					${businessDayCtr.businessRegex}
				</a></td>
				<td>
					<fmt:formatDate value="${businessDayCtr.businessStart}" pattern="yyyy-MM-dd hh:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${businessDayCtr.businessEnd}" pattern="yyyy-MM-dd hh:mm:ss"/>
				</td>
				<td>
					${businessDayCtr.priority}
				</td>
				<td>
					${fns:getDictLabel(businessDayCtr.status,'gold_status','未知')}
				</td>
				<c:if test="true"><td>
					<c:if test="${fns:hasAnyPermission(businessDayCtr.groupStr,'update',businessDayCtr.id,'gold:businessDayCtr:') }">
						<a href="${ctx}/gold/businessDayCtr/preUpdate?id=${businessDayCtr.id}">修改</a>
					</c:if>
					<c:if test="${fns:hasAnyPermission(businessDayCtr.groupStr,'delete',businessDayCtr.id,'gold:businessDayCtr:') }">
						<a href="${ctx}/gold/businessDayCtr/delete?id=${businessDayCtr.id}" onclick="return confirmx('确认要删除该交易吗？', this.href)">删除</a>
					</c:if>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>