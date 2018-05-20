package demo.oauth2.client.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;

import com.alibaba.fastjson.JSON;

public class Oauth2Utils {
	public static final String JsonEncode = "application/json;charset=UTF-8";
	public static final String HtmlEncode = "text/html;charset=UTF-8";
	
	private Oauth2Utils() {}
	
	/*关于OAuth2的错误码及说明*/
	/*10000~19999，系统级错误，包含参数错误，路径错误，登录异常，api授权等问题*/
	/*20000~29999，服务级错误，包含业务逻辑错误等*/
	public static final String OAUTH2_ERR_CODE = "err_code";
	public static final String OAUTH2_ERR_MSG = "err_msg";
	public static final String OAUTH2_ERR_MSG_TYPE = "err_type";
	
	public static final String OAUTH2_ERR_CODE_10001 = "10001";
	public static final String OAUTH2_ERR_CODE_10001_MSG = "d";
	

	public static void logRequest(HttpServletRequest request, Log logger) {
		if("POST".equals(request.getMethod())) {
			StringBuffer names = new StringBuffer();
			names.append(request.getRequestURI()).append("?");
			
    		Enumeration<String> parameterNames = request.getParameterNames();
    		if(parameterNames.hasMoreElements()) {
    			while(parameterNames.hasMoreElements()) {
    				String name = parameterNames.nextElement();
    				names.append(name).append("=").append(request.getParameter(name)).append("&");
    			}
    			names.deleteCharAt(names.length() - 1);
    		}
    		logger.warn(names.toString());
    	} else {
    		logger.warn(request.getRequestURI());
    	}
	}
	
	public static String errMsgToJson(String type, String message, String code) {
		Map<String, String> jsonMap = new HashMap<String, String>();
    	jsonMap.put(Oauth2Utils.OAUTH2_ERR_MSG_TYPE, type);
    	jsonMap.put(Oauth2Utils.OAUTH2_ERR_MSG, message);
    	jsonMap.put(Oauth2Utils.OAUTH2_ERR_CODE, code);
    	return JSON.toJSONString(jsonMap);
	}
}
