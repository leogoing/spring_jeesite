<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>变量添加</title>
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
			}/* else if(${flag=='add'}){//没有必要根据模块来选择
				$('#variablePathDiv .input-append').hide();
				$('#module').change(function(){
					if(this.value!=''){
						$('#variablePathDiv .input-append').show();
					}
				});
			} */
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/gold/variable/list">变量列表</a></li>
		<li class="active"><a href="${ctx}/gold/variable/form?id=${variable.id}">变量添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="variable" action="${ctx}/gold/variable/${flag}" method="post" class="form-horizontal">
		<form:hidden path="id"/> 
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">变量来源：</label>
			<div class="controls">
				<form:select path="dataSource" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_data_source')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变量模块：</label>
			<div class="controls">
				<form:select path="module" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_template_module')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变量路径：</label>
			<div class="controls" id="variablePathDiv">
				<%-- <c:if test="${flag=='add'}">
					<b style="color: #ccc;font-weight: 100;">请先选择模块</b>
				</c:if> --%>
                <sys:treeselect id="variablePath" name="variablePath" value="${variable.variablePath}" labelName="" labelValue="${variable.variablePath}"
					title="变量路径" url="/gold/variable/treeData" cssClass="" notAllowSelectParent="true" checked="false" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变量名称：</label>
			<div class="controls">
				<form:input path="variableName" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变量别称：</label>
			<div class="controls">
				<form:input path="visualName" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变量类型：</label>
			<div class="controls">
				<form:input path="variableType" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">依赖变量：</label>
			<div class="controls" id="variablePathDiv">
				<%-- <c:if test="${flag=='add'}">
					<b style="color: #ccc;font-weight: 100;">请先选择模块</b>
				</c:if> --%>
                <sys:treeselect id="depend" name="depend" value="${variable.depend}" labelName="" labelValue="${variable.depend}" allowInput="true"
					title="依赖变量" url="/gold/variable/variableTree" cssClass="" notAllowSelectParent="true" checked="true" continuation="true" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变量字典名称：</label>
			<div class="controls">
				<form:input path="dict" htmlEscape="false" maxlength="20" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组信息：</label>
			<div class="controls">
                <sys:treeselect id="groupStr" name="groupStr" value="${variable.groupStr}" labelName="groupStrNames" labelValue="${variable.groupStrNames}"
					title="分组" url="/sys/group/treeData?node=variable" cssClass="" notAllowSelectParent="true" checked="true" isGroup="true" node="variable"/>
			</div>
		</div>
		<c:if test="${flag!='add'}">
			<div class="control-group">
				<label class="control-label">操作明细：</label>
				<div class="controls">
	                <sys:operateselect id="operate" module="variable" objectId="${variable.id}"/>
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