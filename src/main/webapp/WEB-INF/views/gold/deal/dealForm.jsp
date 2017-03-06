<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易添加</title>
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
			
			if(${flag=='view' || flag=='approve' }){
				$('.controls input:not(#operate)').attr('disabled','disabled');
			} 
			
			$("#btnApprove").click(function(){
				confirmx('确认要放行该交易吗？',function(){ajax('approve',{"id":${deal.id},
																	"amount":${deal.amount},
																	"bornOf":${deal.bornOf},
																	"buyCur":'${deal.buyCur}',
																	"counterparty.id":${deal.counterparty.id},
																	"dealAccount.id":${deal.dealAccount.id},
																	"dealDate":$('#dealDate').val(),
																	"dealNo":'${deal.dealNo}',
																	"dealType":${deal.dealType},
																	"deliveryDate":$('#deliveryDate').val(),
																	"earlyPay":${deal.earlyPay},
																	"money":${deal.money},
																	"sellCur":'${deal.sellCur}',
																	"sfFlag":${deal.sfFlag},
																	"status":${deal.status},
																	"taskId":'${taskId}',"procInsId":'${procInsId}'},
																	function(res){
																		alert(res.message);
																		document.getElementById('listHref').click();
																	})});
			});
			
			$("#btnReCheck").click(function(){
				confirmx('确认要复核该交易吗？',"${ctx}/gold/deal/reCheck?id=${deal.id}&taskId=${taskId}&procInsId=${procInsId}");
			});
			
			$("#btnBack").click(function(){
				confirmx('确认要踢回该交易吗？',"${ctx}/gold/deal/reBack?id=${deal.id}&taskId=${taskId}&procInsId=${procInsId}");
			});
			
			$("#btnSubmit").click(function(){
				if(${flag=='reCheck'}){
					confirmx('确认要修改并保存该交易吗？',function(){
						ajax('updateNoRedirect',$("#inputForm").serialize()+"&taskId=${taskId}&procInsId=${procInsId}");
					});
				}else{
					$("#inputForm").submit();
				}
			});
			
			$("#btnCancel").click(function(){
				confirmx('确认要取消办理该交易吗？', function(){ajax('cancel',{'taskId':'${taskId}'})});
			});
			
			$("#btnStop").click(function(){
				confirmx('确认要终止办理该交易吗？',function(){ajax('stop',{'taskId':'${taskId}','procInsId':'${procInsId}','dealId':${deal.id}})});
			});
		});
		
		
		function ajax(url,data,func){
			$.ajax({
				url:'${ctx}/gold/deal/'+url,
				data:data,
				dataType:'json',
				type:'post',
				success:function(res){
					if(func){
						func(res);
					}else{
						alert(res.message);
					}
				},
				error:function(){
					alert("error");
				}
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:if test="${flag=='reCheck' || flag=='approve'}">
			<li><a id="listHref" href="${ctx}/gold/deal/${flag}List">交易列表</a></li>
		</c:if>
		<c:if test="${flag!='reCheck' && flag!='approve'}">
			<li><a href="${ctx}/gold/deal/list">交易列表</a></li>
		</c:if>
		<li class="active"><a href="${ctx}/gold/deal/form?id=${deal.id}">交易</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="deal" action="${ctx}/gold/deal/${flag}" method="post" class="form-horizontal">
		<form:hidden path="id"/> 
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">近远端标识：</label>
			<div class="controls">
				<form:radiobuttons path="sfFlag" items="${fns:getDictList('gold_deal_sfFlag')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">模块：</label>
			<div class="controls">
				<form:select path="module" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_template_module')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<c:if test="${flag!='update' && flag!='preUpdate' && flag!='preAdd' && flag!='Add'}">
			<div class="control-group">
				<label class="control-label">流程实例号：</label>
				<div class="controls">
					<form:input path="procInsId" htmlEscape="false" maxlength="20" class="input-xlarge" disabled="true"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">交易编号：</label>
			<div class="controls">
				<form:input path="dealNo" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易类型：</label>
			<div class="controls">
				<form:select path="dealType" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_deal_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">买入币种：</label>
			<div class="controls">
				<form:input path="buyCur" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">卖出币种：</label>
			<div class="controls">
				<form:input path="sellCur" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">买卖数量：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易金额：</label>
			<div class="controls">
				<form:input path="money" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易日期：</label>
			<div class="controls">
				<form:input id="dealDate" path="dealDate" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交割日期：</label>
			<div class="controls">
				<form:input id="deliveryDate" path="deliveryDate" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易来源：</label>
			<div class="controls">
				<form:select path="bornOf" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('gold_deal_bornOf')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">是否提前交割：</label>
			<div class="controls">
				<form:radiobuttons path="earlyPay" items="${fns:getDictList('gold_deal_earlyPay')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易对手：</label>
			<div class="controls">
				<form:select path="counterparty.id" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${counterparties}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">本方账户行：</label>
			<div class="controls">
				<form:select path="dealAccount.id" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${counterparties}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组信息：</label>
			<div class="controls">
                <sys:treeselect id="groupStr" name="groupStr" value="${deal.groupStr}" labelName="groupStrNames" labelValue="${deal.groupStrNames}"
					title="分组" url="/sys/group/treeData?node=deal" cssClass="" notAllowSelectParent="true" checked="true" isGroup="true" node="deal"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">放行状态：</label>
			<div class="controls">
				<form:input path="approve" value="${fns:getDictLabel(deal.approve,'gold_deal_approve','未知')}" 
				htmlEscape="false" maxlength="20" class="input-xlarge required" disabled="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<c:if test="${flag!='add'}">
			<div class="control-group">
				<label class="control-label">操作明细：</label>
				<div class="controls">
	                <sys:operateselect id="operate" module="deal" objectId="${deal.id}"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:radiobuttons path="status" items="${fns:getDictList('gold_deal_status')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
			<c:if test="${actStatus!='claim'}">
				<c:if test="${fns:hasAnyPermission(deal.groupStr,'update',deal.id,'gold:deal:') && flag !='view' && flag!='approve'}">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
				</c:if>
				<c:if test="${flag=='reCheck' && fns:hasAnyPermission(deal.groupStr,'recheck',deal.id,'gold:deal:') }">
					<input id="btnReCheck" class="btn btn-primary" type="button" value="复 核"/>&nbsp;
					<c:if test="${fns:hasAnyPermission(deal.groupStr,'stop',deal.id,'gold:deal:') }">
						<input id="btnStop" class="btn btn-primary" type="button" value="终止流程"/>&nbsp;
					</c:if>
				</c:if>
				<c:if test="${flag=='approve'}">
					<c:if test="${fns:hasAnyPermission(deal.groupStr,'approve',deal.id,'gold:deal:') }">
						<input id="btnApprove" class="btn btn-primary" type="button" value="放 行"/>&nbsp;
					</c:if>
					<c:if test="${fns:hasAnyPermission(deal.groupStr,'reback',deal.id,'gold:deal:') }">
						<input id="btnBack" class="btn btn-primary" type="button" value="踢 回"/>&nbsp;
					</c:if>
				</c:if>
				<c:if test="${flag=='reCheck' || flag=='approve'}">
					<input id="btnCancel" class="btn btn-primary" type="button" value="取消办理"/>&nbsp;
					<!--<c:if test="${fns:hasAnyPermission(deal.groupStr,'appoint',deal.id,'gold:deal:')}">
						<input id="btnAppoint" class="btn btn-primary" type="button" value="委派"/>&nbsp;
					</c:if>-->
				</c:if>
			</c:if>
			<input id="btnReturn" class="btn" type="button" value="返 回" onclick="document.getElementById('listHref').click();"/>
		</div>
	</form:form>
</body>
</html>