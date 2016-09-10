<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>变量管理</title>
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
		<li class="active"><a href="${ctx}/gold/variable/list">变量列表</a></li>
		<shiro:hasPermission name="gold:variable:create">
			<li ><a href="${ctx}/gold/variable/preAdd">变量添加</a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="variable" action="${ctx}/gold/variable/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>变量模块：</label>
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
				<th>变量名称</th>
				<th>变量路径</th>
				<th>变量别称</th>
				<th>变量模块</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="variable">
			<tr>
				<td><a href="${ctx}/gold/variable/detail?id=${variable.id}">
					${variable.variableName}
				</a></td>
				<td>
					${fns:abbr(variable.variablePath,50)}
				</td>
				<td>
					${variable.visualName}
				</td>
				<td>
					${fns:getDictLabel(variable.module,'gold_template_module','未知')}
				</td>
				<c:if test="true"><td>
					<c:if test="${fns:hasAnyPermission(variable.groupStr,'update',variable.id,'gold:variable:') }">
						<a href="${ctx}/gold/variable/preUpdate?id=${variable.id}">修改</a>
					</c:if>
					<c:if test="${fns:hasAnyPermission(variable.groupStr,'delete',variable.id,'gold:variable:') }">
						<a href="${ctx}/gold/variable/delete?id=${variable.id}" onclick="return confirmx('确认要删除该交易吗？', this.href)">删除</a>
					</c:if>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>