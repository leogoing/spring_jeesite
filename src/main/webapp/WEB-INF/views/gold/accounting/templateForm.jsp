<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>模板添加</title>
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
		<li><a href="${ctx}/gold/template/list">模板列表</a></li>
		<li class="active"><a href="${ctx}/gold/template/form?id=${template.id}">模板添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="template" action="${ctx}/gold/template/${flag}" method="post" class="form-horizontal">
		<form:hidden path="id"/> 
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">模板模块：</label>
			<div class="controls">
				<form:select path="module" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_template_module')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">模板名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所符的条件：</label>
			<div class="controls">
                <sys:treeselect id="conditionPass" name="conditionPass.id" value="${template.conditionPass.id}" labelName="" labelValue="${template.conditionPass.name}"
					title="条件" url="/gold/condition/treeData?module=${template.module}" cssClass=" required" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">科目：</label>
			<div class="controls">
                <sys:treeselect id="subject" name="subject.id" value="${template.subject.id}" labelName="" labelValue="${subject.name}"
					title="科目" url="/gold/subject/treeData?module=${template.module}" notAllowSelectParent="true" checked="false" isGroup="true" isSubject="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">一级科目条件：</label>
			<div class="controls">
                <sys:treeselect id="firstSubjectVariable" name="firstSubjectVariable.id" value="${template.firstSubjectVariable.id}" labelName="" 
                	labelValue="${template.firstSubjectVariable.name}" title="条件" url="/gold/condition/treeData?module=${template.module}" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">二级科目条件：</label>
			<div class="controls">
                <sys:treeselect id="secondSubjectVariable" name="secondSubjectVariable.id" value="${template.secondSubjectVariable.id}" labelName="" 
                	labelValue="${template.secondSubjectVariable.name}" title="条件" url="/gold/condition/treeData?module=${template.module}" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">三级科目条件：</label>
			<div class="controls">
                <sys:treeselect id="thirdSubjectVariable" name="thirdSubjectVariable.id" value="${template.thirdSubjectVariable.id}" labelName="" 
                	labelValue="${template.thirdSubjectVariable.name}" title="条件" url="/gold/condition/treeData?module=${template.module}" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">对应科目条件：</label>
			<div class="controls">
                <sys:treeselect id="relativeSubjectVariable" name="relativeSubjectVariable.id" value="${template.relativeSubjectVariable.id}" labelName="" cssClass=" required"
                	labelValue="${template.relativeSubjectVariable.name}" title="条件" url="/gold/condition/treeData?module=${template.module}" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">金额条件：</label>
			<div class="controls">
                <sys:treeselect id="moneyVariable" name="moneyVariable.id" value="${template.moneyVariable.id}" labelName="" 
                	labelValue="${template.moneyVariable.name}" title="条件" url="/gold/condition/treeData?module=${template.module}" cssClass=" required" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">借贷标识条件：</label>
			<div class="controls">
                <sys:treeselect id="dcFlagVariable" name="dcFlagVariable.id" value="${template.dcFlagVariable.id}" labelName="" 
                	labelValue="${template.dcFlagVariable.name}" title="条件" url="/gold/condition/treeData?module=${template.module}" cssClass=" required" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组信息：</label>
			<div class="controls">
                <sys:treeselect id="groupStr" name="groupStr" value="${template.groupStr}" labelName="groupStrNames" labelValue="${template.groupStrNames}"
					title="分组" url="/sys/group/treeData?node=template" cssClass="" notAllowSelectParent="true" checked="true" isGroup="true" node="template"/>
			</div>
		</div>
		<c:if test="${flag!='add'}">
			<div class="control-group">
				<label class="control-label">操作明细：</label>
				<div class="controls">
	                <sys:operateselect id="operate" module="template" objectId="${template.id}"/>
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