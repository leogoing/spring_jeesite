<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>条件配制</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					$("#expression").attr("name","expression");
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
			
			//加载变量
			$("#module").change(function(){
				if(this.value!=''){
					$.ajax({
						url:'${ctx}/gold/variable/jsonList',
						data:{"module":this.value},
						dataType:'json',
						type:'post',
						success:function(res){
							$("#variable").empty();
							if(res.length>0){
								for(var i=0;i<res.length;i++){
									var a="<a class='btn variable edit' id='"+res[i].id+"' value='#"+
											res[i].id+"#' title='"+
											res[i].variableName+"'>"+
											res[i].visualName+"</a>";
									if((i+1)%5==0){
										a+="<br>";
									}
									$(a).appendTo("#variable");
									$("#"+res[i].id).data("dicts",res[i].dictVal);
								}	
							}else{
								$('<b style="color: #ccc;font-weight: 100;">该模块没有变量</b>').appendTo("#variable");
								$("#messageBox").text("无变量信息!");$("#messageBox").show();
							}
							$("<a class='btn variable edit' id='true' value=' true '>通过</a>").appendTo("#variable");
							$("<a class='btn variable edit' id='true' value=' false '>不通过</a>").appendTo("#variable");
						},
						error:function(){
							$("#messageBox").text("加载变量错误!");$("#messageBox").show();
						}
					});
				}
			});
			
			//点击事件
			$("#edit").on("click",".edit",function(){
				$("#text").html($("#text").text()+" "+this.text);
				$("#expression").text($("#expression").text()+$(this).attr('value'));
				textArr.push(this.text);
				expArr.push($(this).attr('value'));
			});
			
			$("#variable").on("click",".variable",function(){
				appendDict(this);
			});
			
			$("#expression").text("${condition.expression}");
			
			$("#module").trigger("change");
			
		});
		
		var textArr=[];//用于保存操作的值,实现撤销功能
		var expArr=[];
		
		function appendDict(ele){//添加变量值选项
			var dicts=$(ele).data("dicts");
			$("#variableVal").empty();
			if(!dicts){
				$('<b style="color: #ccc;font-weight: 100;">该变量不存在变量值</b>').appendTo("#variableVal");
				return ;
			}
			
			for(var i=0;i<dicts.length;i++){
				var a='<a class="btn variableVal edit" value="'+dicts[i].value+'" title="sfFlag">'+dicts[i].label+'</a>'
				if(i%5==0 && i!=0){
					a+="<br>";
				}
				$(a).appendTo("#variableVal");
			}
		}
		
		function revocation(){//撤销操作
			textArr.pop();expArr.pop();
			$("#text").html(textArr.join(' '));
			$("#expression").text(expArr.join(''));
		}
		
		function empty(){//清空操作
			textArr=[];expArr=[];
			$("#text").html('');
			$("#expression").html('');
		}
		
		function print(){
			var val=$("#manually").val().trim();
			if(val!=''){
				var separate="";
				if(isNaN(Number(val))){
					separate="'";
				}
				var str=" "+separate+val+separate+" ";
				$("#text").html($("#text").text()+str);
				$("#expression").html($("#expression").text()+str);
				textArr.push(str);
				expArr.push(str);
			}
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/gold/condition/list">条件列表</a></li>
		<li  class="active"><a href="${ctx}/gold/condition/preAdd">条件</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="condition" action="${ctx}/gold/condition/${flag}" method="post" class="form-horizontal">
		<form:hidden path="id"/> 
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">条件模块：</label>
			<div class="controls">
				<form:select path="module" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_template_module')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">条件名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">条件别称：</label>
			<div class="controls">
				<form:input path="alias" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<p style="font-size: x-large;margin-left: 23%;color: #999;">编辑区</p>
		<div id="edit" class="control-group">
			<c:if test="${flag!='view'}">
				<label class="control-label">如果判断</label>
				<div id="judge" class="controls">
					<a class="btn judge edit" value=" if( ">如果</a>
					<a class="btn judge edit" value=" ){ ">那么</a>
					<a class="btn judge edit" value=" }else if( ">否则如果</a>
					<a class="btn judge edit" value=" }else{ ">否则</a>
					<a class="btn judge edit" value=" } ">结束</a>
					<a class="btn judge edit" value=" return ">确定为</a>
				</div>
				<br>
				<label class="control-label">变量选择</label>
				<div id="variable" class="controls">
					<b style="color: #ccc;font-weight: 100;">请先选择模块</b>
				</div>
				<br>
				<label class="control-label">变量值选择</label>
				<div id="variableVal" class="controls">
					<b style="color: #ccc;font-weight: 100;">请先选择变量</b>
				</div>
				<br>
				<label class="control-label">运算符</label>
				<div id="operator" class="controls">
					<a class="btn operator edit" value=" + ">加</a>
					<a class="btn operator edit" value=" - ">减</a>
					<a class="btn operator edit" value="*">乘</a>
					<a class="btn operator edit" value="/">除</a>
					<a class="btn operator edit" value=" >">大于</a>
					<a class="btn operator edit" value=" &lt;">小于</a>
					<a class="btn operator edit" value="= ">是</a>
					<a class="btn operator edit" value=" == ">等于</a>
					<a class="btn operator edit" value=" !">非</a>
				</div><br>
				<label class="control-label">操作</label>
				<div id="tool" class="controls">
					<input type="text" id="manually" style="width:150px;"/><a class="btn tool" onclick="print();">输入</a>
					<a class="btn tool" onclick="revocation();">撤销</a>
					<a class="btn tool" onclick="empty();">清空</a>
				</div>
				<br>
			</c:if>
			<label class="control-label">条件体</label>
			<div id="condition" class="controls">
				<form:textarea id="text" path="translation" maxlength="4000" readonly="readonly" rows="4" class="input-xlarge"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">条件表达式：</label><!-- 脚本预览 -->
			<div class="controls">
				<form:textarea path="" id="expression" htmlEscape="false" rows="4" maxlength="4000" class="input-xlarge required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">条件附言：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="4000" class="input-xlarge"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组信息：</label>
			<div class="controls">
                <sys:treeselect id="groupStr" name="groupStr" value="${condition.groupStr}" labelName="groupStrNames" labelValue="${condition.groupStrNames}"
					title="分组" url="/sys/group/treeData?node=condition" cssClass="" notAllowSelectParent="true" checked="true" isGroup="true" node="condition"/>
			</div>
		</div>
		<c:if test="${flag!='add'}">
			<div class="control-group">
				<label class="control-label">操作明细：</label>
				<div class="controls">
	                <sys:operateselect id="operate" module="condition" objectId="${condition.id}"/>
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