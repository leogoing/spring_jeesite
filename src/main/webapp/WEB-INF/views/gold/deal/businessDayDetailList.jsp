<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>营业日历史</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
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
			
			$('.controls input:not(#operate)').attr('disabled','disabled');
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/gold/businessDayDetail/list">营业日管理列表</a></li>
		<shiro:hasPermission name="gold:businessDayDetail:create">
			<li ><a href="${ctx}/gold/businessDayDetail/preAdd">营业日管理添加</a></li>
		</shiro:hasPermission>
		<li><a class="active" href="${ctx}/gold/businessDayDetail/historyList">营业日历史列表</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="businessDayDetail" action="${ctx}/gold/businessDayCtr/historyList" method="post" class="form-horizontal">
		<form:hidden path="id"/> 
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">营业日管理正则匹配式：</label>
			<div class="controls">
				<form:input path="businessRegex" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开始营业时间：</label>
			<div class="controls">
				<form:input path="businessStart" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关账时间：</label>
			<div class="controls">
				<form:input path="businessEnd" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关账操作员：</label>
			<div class="controls">
				<form:input path="changeDayUser.name" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优先级：</label>
			<div class="controls">
				<form:input path="priority" htmlEscape="false" maxlength="20" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组信息：</label>
			<div class="controls">
                <sys:treeselect id="groupStr" name="groupStr" value="${businessDayDetail.groupStr}" labelName="groupStrNames" labelValue="${businessDayDetail.groupStrNames}"
					title="分组" url="/sys/group/treeData?node=businessDayDetail" cssClass="" notAllowSelectParent="true" checked="true" isGroup="true" node="businessDayDetail"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">操作明细：</label>
			<div class="controls">
                <sys:operateselect id="operate" module="businessDayDetail" objectId="${businessDayDetail.id}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:radiobuttons path="status" items="${fns:getDictList('gold_status')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>