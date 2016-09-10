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
		<li class="active"><a href="${ctx}/gold/subject/list">科目列表</a></li>
		<shiro:hasPermission name="gold:subject:create">
			<li ><a href="${ctx}/gold/subject/preAdd">科目添加</a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="subject" action="${ctx}/gold/subject/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>科目模块：</label>
				<form:select path="module" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_template_module')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>科目类别：</label>
				<form:select path="subjectType" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_subject_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>科目名称</th>
				<th>科目类别</th>
				<th>一级科目</th>
				<th>二级科目</th>
				<th>科目模块</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="subject">
			<tr>
				<td><a href="${ctx}/gold/subject/detail?id=${subject.id}">
					${subject.name}
				</a></td>
				<td>
					${fns:getDictLabel(subject.subjectType,'gold_subject_type','未知')}
				</td>
				<td>
					${subject.firstSubject}
				</td>
				<td>
					${subject.secondSubject}
				</td>
				<td>
					${fns:getDictLabel(subject.module,'gold_template_module','未知')}
				</td>
				<c:if test="true"><td>
					<c:if test="${fns:hasAnyPermission(subject.groupStr,'update',subject.id,'gold:subject:') }">
						<a href="${ctx}/gold/subject/preUpdate?id=${subject.id}">修改</a>
					</c:if>
					<c:if test="${fns:hasAnyPermission(subject.groupStr,'delete',subject.id,'gold:subject:') }">
						<a href="${ctx}/gold/subject/delete?id=${subject.id}" onclick="return confirmx('确认要删除该交易吗？', this.href)">删除</a>
					</c:if>
				</td></c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>