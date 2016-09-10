<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易机构管理</title>
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
		<li class="active"><a href="${ctx}/gold/counterparty/list">交易机构列表</a></li>
		<shiro:hasPermission name="gold:counterparty:create">
			<li ><a href="${ctx}/gold/counterparty/preAdd">交易机构添加</a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="counterparty" action="${ctx}/gold/counterparty/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>机构编号：</label>
				<form:input path="code" htmlEscape="false" maxlength="200" class="input-medium"/>
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
				<th>交易机构编号</th>
				<th>境内外标识</th>
				<th>交易机构名称</th>
				<th>状态</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="counterparty">
			<tr>
				<td><a href="${ctx}/gold/counterparty/detail?id=${counterparty.id}">
					${counterparty.code}
				</a></td>
				<td>
					${fns:getDictLabel(counterparty.placeFlag,'gold_counterparty_placeFlag','未知')}
				</td>
				<td>
					${counterparty.name}
				</td>
				<td>
					${fns:getDictLabel(counterparty.status,'gold_status','未知')}
				</td>
				<c:if test="true"><td>
					<c:if test="${fns:hasAnyPermission(counterparty.groupStr,'update',counterparty.id,'gold:counterparty:') }">
						<a href="${ctx}/gold/counterparty/preUpdate?id=${counterparty.id}">修改</a>
					</c:if>
					<c:if test="${fns:hasAnyPermission(counterparty.groupStr,'delete',counterparty.id,'gold:counterparty:') }">
						<a href="${ctx}/gold/counterparty/delete?id=${counterparty.id}" onclick="return confirmx('确认要删除该交易吗？', this.href)">删除</a>
					</c:if>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>