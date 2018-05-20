package demo.oauth2.client.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import demo.oauth2.client.HttpUtil;
import demo.oauth2.client.model.oauth2.Oauth2Token;
import demo.oauth2.client.service.front.CommonService;
import demo.oauth2.client.service.oauth2.Oauth2TokenService;

@Controller
public class CommonController {
	protected final transient Log logger = LogFactory.getLog(CommonController.class);
    @Autowired
    private CommonService commonService;
    @Autowired
    private Oauth2TokenService oauth2TokenService;
    
    @RequestMapping(value="/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
    	//默认登录页，它不登录，而是提供一个按钮从认证中心登录
    	return "login";
    }
    
    @RequestMapping(value="/")
    public String index(HttpServletRequest request, HttpServletResponse response) {
    	HttpSession session = request.getSession();
		
    	//最简单的登录认证处理
    	String isLogin = (String) session.getAttribute("isLogin");
    	if(!"true".equals(isLogin)) {
    		//如未登录，跳至登录页去，由用户发起至认证中心的认证请求
    		//当然我们也可以自动发起，这没有问题。
    		try {
				response.sendRedirect("/login");
	        	return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return "index";
    }
    
    @RequestMapping(value="/getToken")
    public String getToken(HttpServletRequest request, HttpServletResponse response) {
    	//这是回调url，由认证服务器回调这个函数处理
    	//当返回值有code时，我们进行token的进一认证及用户信息绑定
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        if(!StringUtils.isEmpty(code) && !StringUtils.isEmpty(state)) {
        	String token = commonService.getTokenByCode(code, state);
        	
        	//在成功获取token后
        	//再次获取用户信息，并与本系统用户信息绑定，并设置为登录状态。
        	String resMsg = commonService.getResourceRealname(token);
    		HttpSession session = request.getSession();
    		
        	String isLogin = (String) session.getAttribute("isLogin");
        	if(!"true".equals(isLogin)) {
        		JSONObject parseObject = JSONObject.parseObject(resMsg);
        		
        		session.setAttribute("username", parseObject.getString("username"));
        		session.setAttribute("isLogin", "true");
        	} else {
        		//刷新过token及缓存了，不需要操作，用户还是登录的。
        	}
        	
        	try {
				response.sendRedirect("/");
	        	return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
        	String urlBase = HttpUtil.getOauth2ServerBaseUrl();
        	String redirectUrl = urlBase + "/oauth2/authorize.html?client_id=1&response_type=code&redirect_uri=http://localhost:8001/getToken&state=state";
        	try {
				response.sendRedirect(redirectUrl);
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    	return null;
    }
    
    @ResponseBody
    @RequestMapping(value="/userinfo")
    public String userinfo(HttpServletRequest request, HttpServletResponse response) {
    	//请求资源，不必多解释
    	HttpSession session = request.getSession();
    	
    	String username = (String) session.getAttribute("username");
    	String isLogin = (String) session.getAttribute("isLogin");
    	if("true".equals(isLogin)) {
    		Oauth2Token oauth2Token = oauth2TokenService.selectByUsernameClientId(username, "1");
    		if(null == oauth2Token) {
        		String respString = Oauth2Utils.errMsgToJson("userinfo", "invalid token", "10002");
                return respString;
        	}
    		
    		try {
        		String resMsg = commonService.getResourceRealname(oauth2Token.getAccessToken());
        		return Oauth2Utils.errMsgToJson("userinfo", resMsg, "200");
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
    		
    	} else {
    		String urlBase = HttpUtil.getOauth2ServerBaseUrl();
        	String redirectUrl = urlBase + "/oauth2/authorize?clientId=1&response_type=code&redirect_uri=http://localhost:8001/getToken&state=state";
        	try {
				response.sendRedirect(redirectUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	return Oauth2Utils.errMsgToJson("userinfo", "login before", "10003");
    }
}