<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公允价格管理</title>
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
		<li class="active"><a href="${ctx}/gold/soundPrice/list">公允价格列表</a></li>
		<shiro:hasPermission name="gold:soundPrice:create">
			<li ><a href="${ctx}/gold/soundPrice/preAdd">公允价格添加</a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="soundPrice" action="${ctx}/gold/soundPrice/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>贵金属品种：</label>
				<form:select path="variety.id" class="input-xlarge">
					<form:option value="" label=""/>
					<form:options items="${varieties}" itemLabel="varietyName" itemValue="id" htmlEscape="false"/>
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
				<th>公允价格</th>
				<th>营业日</th>
				<th>贵金属品种</th>
				<th>公允价格来源</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="soundPrice">
			<tr>
				<td><a href="${ctx}/gold/soundPrice/detail?id=${soundPrice.id}">
					${soundPrice.soundPrice}
				</a></td>
				<td>
					${soundPrice.businessDay}
				</td>
				<td>
					${soundPrice.variety.varietyName}
				</td>
				<td>
					${fns:getDictLabel(soundPrice.priceSource,'gold_price_source','未知')}
				</td>
				<c:if test="true"><td>
					<c:if test="${fns:hasAnyPermission(soundPrice.groupStr,'update',soundPrice.id,'gold:soundPrice:') }">
						<a href="${ctx}/gold/soundPrice/preUpdate?id=${soundPrice.id}">修改</a>
					</c:if>
					<c:if test="${fns:hasAnyPermission(soundPrice.groupStr,'delete',soundPrice.id,'gold:soundPrice:') }">
						<a href="${ctx}/gold/soundPrice/delete?id=${soundPrice.id}" onclick="return confirmx('确认要删除该交易吗？', this.href)">删除</a>
					</c:if>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>