<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易机构添加</title>
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
			
			if(${flag=='view'}){
				$('.controls input:not(#operate)').attr('disabled','disabled');
			}
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/gold/counterparty/list">交易机构列表</a></li>
		<li class="active"><a href="${ctx}/gold/counterparty/form?id=${counterparty.id}">交易机构添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="counterparty" action="${ctx}/gold/counterparty/${flag}" method="post" class="form-horizontal">
		<form:hidden path="id"/> 
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">境内外标识：</label>
			<div class="controls">
				<form:radiobuttons path="placeFlag" items="${fns:getDictList('gold_counterparty_placeFlag')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易机构代号：</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易机构名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组信息：</label>
			<div class="controls">
                <sys:treeselect id="groupStr" name="groupStr" value="${counterparty.groupStr}" labelName="groupStrNames" labelValue="${counterparty.groupStrNames}"
					title="分组" url="/sys/group/treeData?node=counterparty" cssClass="" notAllowSelectParent="true" checked="true" isGroup="true" node="counterparty"/>
			</div>
		</div>
		<c:if test="${flag!='add'}">
			<div class="control-group">
				<label class="control-label">操作明细：</label>
				<div class="controls">
	                <sys:operateselect id="operate" module="counterparty" objectId="${counterparty.id}"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:radiobuttons path="status" items="${fns:getDictList('gold_status')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
			<c:if test="${flag !='view'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>