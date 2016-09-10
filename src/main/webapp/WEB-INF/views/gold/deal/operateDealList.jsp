<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易管理</title>
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
		<li class="active"><a href="${ctx}/gold/deal/${flag}List">交易列表</a></li>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="deal" action="${ctx}/gold/deal/${flag}List" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>交易编号：</label>
				<form:input path="dealNo" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<c:if test="true"><li><label>交易类型：</label>
				<form:radiobuttons path="sfFlag" items="${fns:getDictList('gold_deal_sfFlag')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				<form:radiobuttons path="dealType" items="${fns:getDictList('gold_deal_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li></c:if>
			<c:if test="true"><li><label>状态：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('gold_deal_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>近远端</th>
				<th>交易类型</th>
				<th>买入币种</th>
				<th>卖出币种</th>
				<th>买卖数量</th>
				<th>交易金额</th>
				<th>交割日</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="deal">
			<tr>
				<td><a href="${ctx}/gold/deal/operateDetail?id=${deal.id}&flag=${flag}">
					${deal.dealNo}
				</a></td>
				<td>
					${fns:getDictLabel(deal.sfFlag,'gold_deal_sfFlag','未知')}
				</td>
				<td>
					${fns:getDictLabel(deal.dealType,'gold_deal_type','未知')}
				</td>
				<td>
					${deal.buyCur}
				</td>
				<td>
					${deal.sellCur}
				</td>
				<td>
					${deal.amount}
				</td>
				<td>
					${fns:toMicrometer(deal.money)}
				</td>
				<td>
					<fmt:formatDate value="${deal.deliveryDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					<c:if test="${flag=='approve'}">
						<c:if test="${fns:hasAnyPermission(deal.groupStr,'approve',deal.id,'gold:deal:') }">
							<a href="${ctx}/gold/deal/approve?id=${deal.id}" onclick="return confirmx('确认要放行该交易吗？', this.href)">放行</a>
						</c:if>
						<c:if test="${fns:hasAnyPermission(deal.groupStr,'reback',deal.id,'gold:deal:') }">
							<a href="${ctx}/gold/deal/reBack?id=${deal.id}" onclick="return confirmx('确认要踢回该交易吗？', this.href)">踢回</a>
						</c:if>
					</c:if>
					<c:if test="${flag=='reCheck'}">
						<c:if test="${fns:hasAnyPermission(deal.groupStr,'reCheck',deal.id,'gold:deal:') }">
							<a href="${ctx}/gold/deal/reCheck?id=${deal.id}" onclick="return confirmx('确认要复核该交易吗？', this.href)">复核</a>
						</c:if>
					</c:if>
					<a href="${ctx}/gold/deal/cancel?id=${deal.id}" onclick="return confirmx('确认要取消办理该交易吗？', this.href)">取消办理</a>
					<c:if test="${fns:hasAnyPermission(deal.groupStr,'appoint',deal.id,'gold:deal:')}">
						<a href="${ctx}/gold/deal/appoint?id=${deal.id}" onclick="">委派</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>