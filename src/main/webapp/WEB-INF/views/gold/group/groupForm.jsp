<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/group/list">分组列表</a></li>
		<li class="active"><a href="${ctx}/sys/group/form?id=${group.id}&parentId=${group.parentId}">分组明细</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="group" action="${ctx}/sys/group/${flag}" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级分组:</label>
			<div class="controls">
                <sys:treeselect id="group" name="parentId" value="${group.parentId}" labelName="parentName" labelValue="${group.parentName}"
					title="分组" url="/sys/group/treeData" extId="${group.id}" cssClass="" allowClear="false"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组名称:</label>
			<div class="controls">
				<form:input path="groupId" htmlEscape="false" maxlength="50" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组别称:</label>
			<div class="controls">
				<form:input path="groupName" htmlEscape="false" maxlength="50"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组状态:</label>
			<div class="controls">
				<form:select path="status" class="input-medium">
					<form:options items="${fns:getDictList('gold_group_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="form-actions">
			<c:if test="${flag!='view'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>