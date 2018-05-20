package demo.oauth2.resource.controller.api.v1;

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
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import demo.oauth2.resource.HttpUtil;
import demo.oauth2.resource.controller.Oauth2Utils;
import demo.oauth2.resource.model.front.FrontUser;
import demo.oauth2.resource.model.oauth2.Oauth2Token;
import demo.oauth2.resource.service.front.FrontUserService;
import demo.oauth2.resource.service.oauth2.Oauth2TokenService;

@Controller
public class UserinfoController {
	protected final transient Log logger = LogFactory.getLog(UserinfoController.class);
    @Autowired
    private FrontUserService frontUserService;
    @Autowired
    private Oauth2TokenService oauth2TokenService;
    
    @ResponseBody
    @RequestMapping(value="/oauth2/api/userinfo")
    public String userInfo(HttpServletRequest request, HttpServletResponse response) throws OAuthSystemException {
        try {
            //构建OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
            //获取Access Token
            String accessToken = oauthRequest.getAccessToken();

            Oauth2Token oauth2Token = oauth2TokenService.selectByAccessToken(accessToken);
            
            //没有保存的token，需从认证中心验证
            if(oauth2Token == null) {
            	//当本地缓存没有时，向服务器验证token
            	//注意，这里不需要刷新token，它只是验证是否可以下发资源
            	String result = HttpUtil.get(HttpUtil.getOauth2ServerBaseUrl() + "/oauth2/check.html?access_token=" + accessToken);
            	JSONObject jsonToken = JSONObject.parseObject(result);
            	
            	if(!"true".equals(jsonToken.getString("success"))) {
            		String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid token, missing token.", "20001");
            		return respString;
            	}
            	
            	//验证完成，保存到token缓存（可以是数据库，也可以是redis中，实际redis自带过期时间，也很好。但数据库也不错，在流量不太大时完全可以灵活的管理）
            	oauth2Token = new Oauth2Token();
            	oauth2Token.setAccessToken(jsonToken.getString("access_token"));
            	oauth2Token.setExpireIn(jsonToken.getDate("expire_in"));
            	oauth2Token.setScope(jsonToken.getString("scope"));
            	oauth2Token.setUsername(jsonToken.getString("username"));
            	
            	oauth2TokenService.save(oauth2Token);
            }
            
            //验证Access Token
            if (oauth2TokenService.checkExpireIn(oauth2Token.getExpireIn().getTime())) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid token, token is expired.", "20002");
                return respString;
            }
            
            FrontUser user = frontUserService.selectByUsername(oauth2Token.getUsername());
            
            if(user == null) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid resource, user is null.", "20003");
                return respString;
            }
            Map<String, String> jsonMap = new HashMap<String, String>();
        	jsonMap.put("username", user.getUsername());
        	jsonMap.put("realname", user.getRealname());
        	jsonMap.put("nickname", user.getNickname());
        	
            String respString = Oauth2Utils.errMsgToJson("resource", JSON.toJSONString(jsonMap), "200");
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
        } catch (Exception e) {
            String respString = Oauth2Utils.errMsgToJson("500", "server internal error", "20102");
            return respString;
		}
    }
}