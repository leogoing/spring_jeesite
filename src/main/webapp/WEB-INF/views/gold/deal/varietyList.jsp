<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品管理</title>
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
		<li class="active"><a href="${ctx}/gold/variety/list}">产品列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="variety" action="${ctx}/gold/variety/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>币别：</label>
				<form:input path="currency" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<li><label>类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_variety_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<c:if test="true"><li><label>状态：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('gold_variety_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li></c:if>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>币种</th>
				<th>品种名</th>
				<th>库存</th>
				<th>单位</th>
				<th>分组</th>
				<th>状态</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="variety">
			<tr>
				<td>
					${variety.currency}(${variety.currencyName})
				</td>
				<td><a href="${ctx}/gold/variety/detail?id=${variety.id}">
					${variety.varietyName}
				</a></td>
				<td>
					${variety.storage}
				</td>
				<td>
					${variety.unit}
				</td>
				<td>
					${variety.groupStr}
				</td>
				<td>
					${fns:getDictLabel(variety.status,'gold_variety_status','未知')}
				</td>
				<c:if test="true"><td>
					<c:if test="${fns:hasAnyPermssion(variety.groupStr,'update',variety.id) }">
						<a href="${ctx}/gold/variety/preUpdate?id=${variety.id}">修改</a>
					</c:if>
					<c:if test="${fns:hasAnyPermssion(variety.groupStr,'delete',variety.id) }">
						<a href="${ctx}/gold/variety/delete?id=${variety.id}" onclick="return confirmx('确认要删除该通知吗？', this.href)">删除</a>
					</c:if>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>