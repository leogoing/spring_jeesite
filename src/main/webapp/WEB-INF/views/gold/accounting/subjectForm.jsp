<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>科目添加</title>
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
		<li><a href="${ctx}/gold/subject/list">科目列表</a></li>
		<li class="active"><a href="${ctx}/gold/subject/form?id=${subject.id}">科目添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="subject" action="${ctx}/gold/subject/${flag}" method="post" class="form-horizontal">
		<form:hidden path="id"/> 
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">科目模块：</label>
			<div class="controls">
				<form:select path="module" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_template_module')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">科目名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">科目类别：</label>
			<div class="controls">
				<form:select path="subjectType" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_subject_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">一级科目：</label>
			<div class="controls">
				<form:input path="firstSubject" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">二级科目：</label>
			<div class="controls">
				<form:input path="secondSubject" htmlEscape="false" maxlength="20" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">三级科目：</label>
			<div class="controls">
				<form:input path="thirdSubject" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">科目余额：</label>
			<div class="controls">
				<form:input path="balance" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组信息：</label>
			<div class="controls">
                <sys:treeselect id="groupStr" name="groupStr" value="${subject.groupStr}" labelName="groupStrNames" labelValue="${subject.groupStrNames}"
					title="分组" url="/sys/group/treeData?node=subject" cssClass="" notAllowSelectParent="true" checked="true" isGroup="true" node="subject"/>
			</div>
		</div>
		<c:if test="${flag!='add'}">
			<div class="control-group">
				<label class="control-label">操作明细：</label>
				<div class="controls">
	                <sys:operateselect id="operate" module="subject" objectId="${subject.id}"/>
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