<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>条件管理</title>
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
		<li class="active"><a href="${ctx}/gold/condition/list">条件列表</a></li>
		<shiro:hasPermission name="gold:condition:create">
			<li ><a href="${ctx}/gold/condition/preAdd">条件添加</a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="condition" action="${ctx}/gold/condition/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>条件模块：</label>
				<form:select path="module" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_template_module')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
				<th>条件名称</th>
				<th>条件表达式</th>
				<th>条件别称</th>
				<th>条件模块</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="condition">
			<tr>
				<td><a href="${ctx}/gold/condition/detail?id=${condition.id}">
					${condition.name}
				</a></td>
				<td>
					${fns:abbr(condition.translation,50)}
				</td>
				<td>
					${condition.alias}
				</td>
				<td>
					${fns:getDictLabel(condition.module,'gold_template_module','未知')}
				</td>
				<c:if test="true"><td>
					<c:if test="${fns:hasAnyPermission(condition.groupStr,'update',condition.id,'gold:condition:') }">
						<a href="${ctx}/gold/condition/preUpdate?id=${condition.id}">修改</a>
					</c:if>
					<c:if test="${fns:hasAnyPermission(condition.groupStr,'delete',condition.id,'gold:condition:') }">
						<a href="${ctx}/gold/condition/delete?id=${condition.id}" onclick="return confirmx('确认要删除该交易吗？', this.href)">删除</a>
					</c:if>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>