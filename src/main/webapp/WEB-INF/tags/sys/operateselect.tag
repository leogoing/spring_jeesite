<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true" description="编号"%>
<%@ attribute name="objectId" type="java.lang.Integer" required="false" description="实例id"%>
<%@ attribute name="module" type="java.lang.String" required="false" description="模块"%>
<%@ attribute name="userId" type="java.lang.String" required="false" description="用户id"%>
<div class="input-append">
	<input id="${id}" readonly="readonly" type="text"  
		/><a id="${id}Button" href="javascript:" class="btn" >&nbsp;<i class="icon-search"></i>&nbsp;</a>&nbsp;&nbsp;
</div>
<script type="text/javascript">
	$("#${id}Button, #${id}").click(function(){
		if("${objectId}"!=null && "${module}"!=null && "${module}"!="" || "${userId}"!=null){
			// 正常打开	
			top.$.jBox.open("iframe:${ctx}/gold/operate/list?objectId=${objectId}&module=${module}&user.id=#{userId}&view=jbox", 
				"操作明细", 600, 420,{buttons:{"关闭":true}});
		}
	});
	
</script>