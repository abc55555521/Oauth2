package demo.oauth2.server.controller;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import demo.oauth2.server.model.front.FrontUser;
import demo.oauth2.server.model.oauth2.Oauth2Client;
import demo.oauth2.server.model.oauth2.Oauth2Code;
import demo.oauth2.server.model.oauth2.Oauth2Token;
import demo.oauth2.server.service.front.FrontUserService;
import demo.oauth2.server.service.oauth2.Oauth2ClientService;
import demo.oauth2.server.service.oauth2.Oauth2CodeService;
import demo.oauth2.server.service.oauth2.Oauth2TokenService;

@Controller
public class AccessTokenController {
    @Autowired
    private Oauth2ClientService oauth2ClientService;
    @Autowired
    private Oauth2CodeService oauth2CodeService;
    @Autowired
    private Oauth2TokenService oauth2TokenService;

    @Autowired
    private FrontUserService frontUserService;
    
    @ResponseBody
    @RequestMapping(value="/oauth2/check", produces={Oauth2Utils.HtmlEncode})
    public String check(HttpServletRequest request) throws URISyntaxException, OAuthSystemException {
    	String token = request.getParameter("access_token");
    	Oauth2Token oauth2Token = oauth2TokenService.selectByAccessToken(token);
    	
    	if(oauth2Token == null){
        	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid token, missing access_token", "10106");
            return respString;
    	}
    	
    	//refresh_token 只有在 access_token 过期时才能使用，并且只能使用一次。当换取到的 access_token 再次过期时，使用新的 refresh_token 来换取 access_token
    	if(oauth2TokenService.checkExpireIn(oauth2Token.getExpireIn().getTime())) {
        	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid token, access_token is expired", "10105");
            return respString;
    	}
    	
    	Map<String, String> jsonMap = new HashMap<String, String>();
    	jsonMap.put("access_token", oauth2Token.getAccessToken());
    	jsonMap.put("expire_in", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(oauth2Token.getExpireIn()));
    	jsonMap.put("scope", "basic");
    	jsonMap.put("username", oauth2Token.getUsername());
    	jsonMap.put("success", "true");
    	
    	return JSON.toJSONString(jsonMap);
    }
    
    @ResponseBody
    @RequestMapping(value="/oauth2/token", produces={Oauth2Utils.HtmlEncode})
    public String token(HttpServletRequest request) throws URISyntaxException, OAuthSystemException {
    	try {
            //构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
            String clientId = oauthRequest.getClientId();
            Oauth2Client client = oauth2ClientService.selectByClientId(clientId);
            
            //检查提交的客户端id是否正确
            if (client == null) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT, "invalid client id", "10101");
                return respString;
            }

            // 检查客户端安全KEY是否正确，这里用非对称加密方式核对安全码
            if (!oauth2ClientService.checkClientSecret(client, oauthRequest.getClientSecret())) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT, "invalid client secret", "10102");
                return respString;
            }
            
            Oauth2Token oauth2Token = null;
            String state = oauthRequest.getParam(OAuth.OAUTH_STATE);
            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
            	
            	String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            	Oauth2Code oauth2Code = oauth2CodeService.selectByAuthCode(authCode, clientId);
            	
                if(oauth2Code == null) {
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid code, missing code", "10103");
                    return respString;
                }
                
            	if(oauth2TokenService.checkExpireIn(oauth2Code.getExpireIn().getTime())) {
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid code, code is expired", "10104");
                    return respString;
                }
            	oauth2Token = oauth2TokenService.selectByUsernameClientId(oauth2Code.getUsername(), clientId);
            	if(oauth2Token == null) {
                	oauth2Token = new Oauth2Token();
                    oauth2Token.setUsername(oauth2Code.getUsername());
                    oauth2Token.setClientId(clientId);
                    
                    oauth2TokenService.save(oauth2Token);
                }
            	//只能使用一次code
            	oauth2CodeService.deleteById(oauth2Code.getModelId());
            	
            } else if(oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.REFRESH_TOKEN.toString())){
            	String refresh_token = oauthRequest.getParam(OAuth.OAUTH_REFRESH_TOKEN);
            	oauth2Token = oauth2TokenService.selectByRefreshToken(refresh_token);
            	
            	if(oauth2Token == null){
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid refreshToken, missing refreshToken", "10106");
                    return respString;
            	}
            	
            	//refresh_token 只有在 access_token 过期时才能使用，并且只能使用一次。当换取到的 access_token 再次过期时，使用新的 refresh_token 来换取 access_token
            	if(!oauth2TokenService.checkExpireIn(oauth2Token.getExpireIn().getTime())) {
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid refreshToken, refreshToken is not expired", "10105");
                    return respString;
            	}        
            } else if(oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.PASSWORD.toString())){
            	String username = oauthRequest.getParam(OAuth.OAUTH_USERNAME);
            	if(username == null || username.length() == 0){
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid username, missing username", "10107");
                    return respString;
            	}
            	
            	FrontUser frontUser = frontUserService.selectByUsername(username);
            	if(frontUser == null){
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid username, unkown username", "10108");
                    return respString;
            	}
            	
            	//校验密码,传过来已经是公钥加密的
                String password = oauthRequest.getParam(OAuth.OAUTH_PASSWORD);
                if(password == null || password.length() == 0){
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid password, missing password", "10109");
                    return respString;
            	}
                //等会按密码加密方式修改
                if(!frontUser.getPassword().equals(password)) {
                	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_GRANT, "invalid password", "10110");
                    return respString;
                }
                oauth2Token = oauth2TokenService.selectByUsernameClientId(username, clientId);
                if(oauth2Token == null) {
                	oauth2Token = new Oauth2Token();
                    oauth2Token.setUsername(username);
                    oauth2Token.setClientId(clientId);
                    
                    oauth2TokenService.save(oauth2Token);
                }
            }

            //生成Access Token 和 Refresh Token并保存
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            final String accessToken = oauthIssuerImpl.accessToken();
            final String refreshToken = oauthIssuerImpl.refreshToken();
            oauth2Token.setAccessToken(accessToken);
            oauth2Token.setRefreshToken(refreshToken);
            long timeStamp = new Date().getTime() + 3600000L;//刷新过期时间
            oauth2Token.setExpireIn(new Timestamp(timeStamp));
            
            oauth2TokenService.save(oauth2Token);

        	Map<String, String> jsonMap = new HashMap<String, String>();
        	jsonMap.put("access_token", accessToken);
        	jsonMap.put("expire_in", String.valueOf(oauth2ClientService.getExpireIn()));
        	jsonMap.put("refresh_token", refreshToken);
        	jsonMap.put("state", state);
        	jsonMap.put("scope", "basic");
        	jsonMap.put("err_code", "200");
        	
            String respString = JSON.toJSONString(jsonMap);
            return respString;
        } catch (OAuthProblemException e) {
            //构建错误响应
        	String respString = Oauth2Utils.errMsgToJson(e.getError(), e.getDescription(), "10101");
        	return respString;
        } catch (OAuthSystemException e) {
        	String respString = Oauth2Utils.errMsgToJson(e.getMessage(), e.getMessage(), "10101");
        	return respString;
        }
    }
}
