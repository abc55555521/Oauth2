$(function () {
	$("#sub").click(function () {
		var id = $("#id").val();
		
		var coin = $("#coin").val();
		
		var websiteId = $("#websiteId").val();
			
		var name = $("#name").val();
		
		var ic = $("#ic").val();
		var icReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
		
		var qq = $("#qq").val();
		var qqReg = /^[1-9][0-9]{4,9}$/;
		
		var wx = $("#wx").val();
		var wxReg = /^[a-zA-Z\d_]{5,}$/;
		
		var nickname = $("#nickname").val();
		var nicknameReg = /^[\u4E00-\u9FA5A-Za-z0-9_]{1,6}$/;
		
		var phone = $("#phone").val();
		var phoneReg = /^1[3|4|5|8][0-9]\d{4,8}$/;
		
		var advert = $("#advert").val();
		var advertReg = /^[\u4E00-\u9FA5A-Za-z0-9_]{1,12}$/;
		
		var contName = $("#contName").val();
		var contPhone = $("#contPhone").val();
		
		var headImg = $("#hidImg1").val();
		var icImgFront = $("#hidImg2").val();
		var icImgBack = $("#hidImg3").val();
		var handIcImg = $("#hidImg4").val();
		var moneyImg = $("#hidImg5").val();
		var ewmImg = $("#hidImg6").val();
		
		var fee = $("#fee").val();
		var feeReg = /^[1-9]\d*\.\d*|0\.\d*[1-9]\d*|[1-9]\d*$/;
		
		var usdcQty = $("#usdcQty").val();
		var usdcQtyReg = /^[1-9]\d*\.\d*|0\.\d*[1-9]\d*|[1-9]\d*$/;
		
		if(name == null || name == "") {
			$("#name").focus();
			layer.tips('姓名不能为空！', '#name', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}
		
		if(ic == null || ic == "") {
			$("#ic").focus();
			layer.tips('身份证号不能为空！', '#ic', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}else if(!icReg.test(ic)) {
			$("#ic").focus();
			layer.tips('身份证号不符合规范！', '#ic', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}
		
		if(qq == null || qq == "") {
			$("#qq").focus();
			layer.tips('qq号不能为空！', '#qq', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}else if(!qqReg.test(qq)) {
			$("#qq").focus();
			layer.tips('qq号不符合规范！', '#qq', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}

		if(wx == null || wx == "") {
			$("#wx").focus();
			layer.tips('微信号不能为空！', '#wx', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}/*else if(!wxReg.test(wx)) {
			$("#wx").focus();
			layer.tips('微信号不符合规范！', '#wx', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}*/
		
		if(nickname == null || nickname == "") {
			$("#nickname").focus();
			layer.tips('昵称不能为空！', '#nickname', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}else if(!nicknameReg.test(nickname)) {
			$("#nickname").focus();
			layer.tips('昵称不能有特殊字符且最多6位！', '#nickname', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}
		
		if(phone == null || phone == "") {
			$("#phone").focus();
			layer.tips('手机号不能为空！', '#phone', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}else if(!phoneReg.test(phone)) {
			$("#phone").focus();
			layer.tips('手机号不符合规范！', '#phone', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}
		
		/*if(advert == null || advert == "") {
			$("#advert").focus();
			layer.tips('广告语不能为空！', '#advert', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}else if(!advertReg.test(advert)) {
			$("#advert").focus();
			layer.tips('广告语不能有特殊字符且最多12位！', '#advert', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}*/
		
		/*if(advert != null && advert != "") {
			if(!advertReg.test(advert)) {
				$("#advert").focus();
				layer.tips('广告语不能有特殊字符且最多12位！', '#advert', {
					tips: [2, '#53a4f4'],
					time: 3000
				});
				return false;
			}
		}*/
		
		if(contPhone != null && contPhone != "") {
			if(!phoneReg.test(contPhone)) {
				$("#contPhone").focus();
				layer.tips('手机号不符合规范！', '#contPhone', {
					tips: [2, '#53a4f4'],
					time: 3000
				});
				return false;
			}
		}
		
		/*if(headImg == null || headImg == "") {
			layer.msg("头像不能为空！");
			return false;
		}
		if(icImgFront == null || icImgFront == "") {
			layer.msg("身份证正面不能为空！");
			return false;
		}
		if(icImgBack == null || icImgBack == "") {
			layer.msg("身份证反面不能为空！");
			return false;
		}
		if(handIcImg == null || handIcImg == "") {
			layer.msg("手持身份证及承诺书照片不能为空！");
			return false;
		}
		if(moneyImg == null || moneyImg == "") {
			layer.msg("资产证明不能为空！");
			return false;
		}
		if(ewmImg == null || ewmImg == "") {
			layer.msg("二维码图片不能为空！");
			return false;
		}*/
		
		var isAssurer = "";
		if ($("#isAssurer1").attr("checked")) {
			isAssurer = 1
        } else {
        	isAssurer = 0
        }
		
		/*if(fee == null || fee == "") {
			$("#fee").focus();
			layer.tips('费率不能为空！', '#fee', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}else if(!feeReg.test(fee)) {
			$("#fee").focus();
			layer.tips('费率格式不符合规范！', '#fee', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}
		
		if(usdcQty == null || usdcQty == "") {
			$("#usdcQty").focus();
			layer.tips('USDC数量不能为空！', '#usdcQty', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}else if(!usdcQtyReg.test(usdcQty)) {
			$("#usdcQty").focus();
			layer.tips('USDC数量格式不符合规范！', '#usdcQty', {
				tips: [2, '#53a4f4'],
				time: 3000
			});
			return false;
		}*/
		
		if(fee != null && fee != "") {
			if(!feeReg.test(fee)) {
				$("#fee").focus();
				layer.tips('费率格式不符合规范！', '#fee', {
					tips: [2, '#53a4f4'],
					time: 3000
				});
				return false;
			}
		}
		if(usdcQty != null && usdcQty != "") {
			if(!usdcQtyReg.test(usdcQty)) {
				$("#usdcQty").focus();
				layer.tips('USDC数量格式不符合规范！', '#usdcQty', {
					tips: [2, '#53a4f4'],
					time: 3000
				});
				return false;
			}
		}
		
		$.ajax({
			type:'post',
			dataType:'json',
			url:'/assurer/addAssurer',
			data:{
				id: id,
				virtualCoinTypeId: coin,
				websiteId:websiteId,
				realName: name,
				idNumber: ic,
				qq: qq,
				wxId: wx,
				nickName: nickname,
				mobile: phone,
				advert: advert,
				emergencyContactName: contName,
				emergencyContactTel: contPhone,
				headImage: headImg,
				idCardFront: icImgFront,
				idCardBack: icImgBack,
				idCardHandheld: handIcImg,
				certificate: moneyImg,
				wxImage: ewmImg,
				isAssurer: isAssurer,
				usdcQty: usdcQty,
				rechargeRate: fee,
				withdrawRate: fee 
			},
			success:function(data, statusText, xhr, $form) {
				layer.msg(data.msg);
				setTimeout(function(){
					if(websiteId != null && websiteId != ''){
						window.location.href = '/assurer/index';
					}else{
						location.reload();
					}
				},1500)
			}
		});
	});
	
	$("#coin").change(function(){
	    var w = $("#coin").val();
	    $.ajax({
	        url:'/assurer/getWebsiteList',
	        async:false,
	        type:'post',
	        data:{id:w},
	        success:function(data){
	        	console.log(data);
	        	var website = $("#websiteId").empty();
	        	var webList = data.result.webList;
	            for ( var i = 0; i < webList.length; i++) {
	            	website.append("<option value='"+webList[i].fid+"'>"+ webList[i].ftitle+"</option>");
	            } 
	        }
	    })
	}); 
})