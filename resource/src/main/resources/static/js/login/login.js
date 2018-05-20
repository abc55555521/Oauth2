$(function(){
	$(document).ready(function(){$(".i-checks").iCheck({checkboxClass:"icheckbox_square-green",radioClass:"iradio_square-green",})});
	$('#drag').drag();
});

function checkMethod(){
	var username=$('#username').val();
	if(username==''){
		layer.msg('您好，请输入用户名！', {icon: 5}); 
		$("#message").html("您好，请输入用户名！");
		$("#message").show();
		return;
	}
	
	var password=$('#password').val();
	if(password==''){
		layer.msg('您好，请输入密码！', {icon: 5}); 
		$("#message").html("您好，请输入密码！");
		$("#message").show();
		return;
	}
	var vildcode=$('#vildcode').val();
	if(vildcode=='' || vildcode=='0'){
		layer.msg('您好，请拖动滑块验证！', {icon: 5}); 
		$("#message").html("您好，请重新拖动滑块！");
		$("#message").show();
		return;
	}
	$("#login_form").submit();
}
