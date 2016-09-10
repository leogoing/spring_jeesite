<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>操作明细</title>
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
	<c:if test="${view}!='jbox'">
		<ul class="nav nav-tabs">
			<li class="active"><a href="${ctx}/gold/operate/list">操作明细列表</a></li>
		</ul><br/>
		<form:form id="searchForm" modelAttribute="operate" action="${ctx}/gold/operate/list" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<ul class="ul-form">
				<li><label>营业日期：</label>
					<form:input path="businessDay" htmlEscape="false" maxlength="200" class="input-medium"/>
				</li>
				<li><label>模块：</label>
					<form:input path="module" htmlEscape="false" maxlength="200" class="input-medium"/>
				</li>
				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li class="clearfix"></li>
			</ul>
		</form:form>
	</c:if>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>操作员</th>
				<th>操作模块</th>
				<th>操作来源</th>
				<th>操作类型</th>
				<th>操作时间</th>
				<th>实例id</th>
				<th>附言</th>
				<th>营业日期</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="operate">
			<tr>
				<td>
					${operate.user.name}
				</td>
				<td>
					${operate.module}
				</td>
				<td>
					${operate.operateSource}
				</td>
				<td>
					${fns:getDictLabel(operate.operateType,'gold_operate_type','未知')}
				</td>
				<td>
					<fmt:formatDate value="${operate.operateTime}" pattern="yyyy-MM-dd hh:mm"/>
				</td>
				<td>
					${operate.objectId}
				</td>
				<td>
					${operate.remarks}
				</td>
				<td>
					${operate.businessDay}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>