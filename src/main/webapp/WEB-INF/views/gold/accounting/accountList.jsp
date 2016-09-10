<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>科目管理</title>
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
		<li class="active"><a href="${ctx}/gold/account/list">账务列表</a></li>
		<shiro:hasPermission name="gold:account:create">
			<li ><a href="${ctx}/gold/account/preAdd">账务添加</a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="account" action="${ctx}/gold/account/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>业务种类：</label>
				<form:select path="businessLines" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_business_line')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>交易编号：</label>
				<form:input path="dealNo" htmlEscape="false" maxlength="200" class="input-medium"/>
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
				<th>交易编号</th>
				<th>一级科目</th>
				<th>二级科目</th>
				<th>三级科目</th>
				<th>借贷方向</th>
				<th>金额</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="account">
			<tr>
				<td><a href="${ctx}/gold/account/detail?id=${account.id}">
					${account.dealNo}
				</a></td>
				<td>
					${account.firstSubject}
				</td>
				<td>
					${account.secondSubject}
				</td>
				<td>
					${account.thirdSubject}
				</td>
				<td>
					${fns:getDictLabel(account.dcFlag,'gold_dcFlag','未知')}
				</td>
				<td>
					${account.money}
				</td>
				<c:if test="true"><td>
						<a href="${ctx}/gold/account/preUpdate?id=${account.id}">修改</a>
						<a href="${ctx}/gold/account/delete?id=${account.id}" onclick="return confirmx('确认要删除该交易吗？', this.href)">删除</a>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>