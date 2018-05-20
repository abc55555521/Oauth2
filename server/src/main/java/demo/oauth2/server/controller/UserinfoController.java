package demo.oauth2.server.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import demo.oauth2.server.model.front.FrontUser;
import demo.oauth2.server.model.oauth2.Oauth2Token;
import demo.oauth2.server.service.front.FrontUserService;
import demo.oauth2.server.service.oauth2.Oauth2TokenService;

@Controller
public class UserinfoController {
	protected final transient Log logger = LogFactory.getLog(UserinfoController.class);
    @Autowired
    private FrontUserService frontUserService;
    @Autowired
    private Oauth2TokenService oauth2TokenService;
    
    @ResponseBody
    @RequestMapping(value="/oauth2/userinfo")
    public String userInfo(HttpServletRequest request, HttpServletResponse response) throws OAuthSystemException {
        try {
            //构建OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
            //获取Access Token
            String accessToken = oauthRequest.getAccessToken();

            Oauth2Token oauth2Token = oauth2TokenService.selectByAccessToken(accessToken);
            
            if(oauth2Token == null) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid token, missing token.", "20001");
                return respString;
            }
            
            //验证Access Token
            if (oauth2TokenService.checkExpireIn(oauth2Token.getExpireIn().getTime())) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid token, token is expired.", "20002");
                return respString;
            }
            FrontUser user = frontUserService.selectByUsername(oauth2Token.getUsername());
            
            Map<String, Object> jsonMap = new HashMap<>();
        	jsonMap.put("realname", user.getRealname());
        	jsonMap.put("code", "200");
        	jsonMap.put("success", true);
        	
            String respString = JSON.toJSONString(jsonMap);
            return respString;
        } catch (OAuthProblemException e) {
            //检查是否设置了错误码
            String errorCode = e.getError();
            if (OAuthUtils.isEmpty(errorCode)) {
            	String respString = Oauth2Utils.errMsgToJson(String.valueOf(HttpStatus.UNAUTHORIZED), e.getDescription(), "20101");
                return respString;
            }
            String respString = Oauth2Utils.errMsgToJson(String.valueOf(e.getError()), e.getDescription(), "20102");
            return respString;
        }
    }
}