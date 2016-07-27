<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分组管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, rootId = "${not empty group.id ? group.id : '0'}";
			addRow("#treeTableList", tpl, data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 5});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
					$(list).append(Mustache.render(tpl, {
						dict: {
							status: getDictLabel(${fns:toJson(fns:getDictList('gold_group_status'))}, row.status)
						}, pid: (row.parentId == pid?0:pid), row: row
					}));
//					addRow(list, tpl, data, row.id);
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/group/list">分组列表</a></li>
		<shiro:hasPermission name="gold:group:update"><li><a href="${ctx}/sys/group/form?parentId=${group.id}">分组添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>分组名称</th><th>分组别称</th><th>分组状态</th><shiro:hasPermission name="sys:group:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/sys/group/form?id={{row.id}}">{{row.groupId}}</a></td>
			<td>{{row.groupName}}</td>
			<td>{{dict.status}}</td>
			<shiro:hasPermission name="gold:group:update"><td>
				<a href="${ctx}/sys/group/form?id={{row.id}}">修改</a>
				<a href="${ctx}/sys/group/delete?id={{row.id}}" onclick="return confirmx('要删除该分组及所有子分组项吗？', this.href)">删除</a>
				<a href="${ctx}/sys/group/form?parentId={{row.id}}">添加下级分组</a> 
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>