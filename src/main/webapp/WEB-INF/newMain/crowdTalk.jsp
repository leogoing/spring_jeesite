<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<script type="text/javascript" src="/jeesite/static/jquery/jquery-1.9.1.js"></script>
	<title>群聊</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//deferred();
		});
		
		function deferred(){
		    $.get('/jeesite/webqq/messageMonitor',function(data){
		        console.log(data);
		        $("#textBoard").val($("#textBoard").val()+"\n\r"+data.ip+":\r"+data.mes);
		        deferred();//每次请求完成,再发一次请求,避免客户端定时刷新来获取数据
		    });
		}
		
		function call(){
			$.ajax({
				url:'/jeesite/webqq/sendMessage',
				data:{mes:$('#input').val()},
				dataType:'json',
				type:'post',
				success:function(res){
					console.log(res);
				},
				error:function(){
					alert("error");
				}
			});
		}
		
	</script>
</head>
<body>
	<h1>Hi!</h1>
	<br>
	<textarea id="textBoard" rows="20" cols="30">
		
	</textarea>
	<br>
	<input type="text" id="input">
	<input type="button" value="发送" onclick="call()"/>
	
</body>
</html>