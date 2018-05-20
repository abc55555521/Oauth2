package demo.oauth2.client.service.front.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import demo.oauth2.client.HttpUtil;
import demo.oauth2.client.model.front.FrontUser;
import demo.oauth2.client.model.oauth2.Oauth2Token;
import demo.oauth2.client.service.front.CommonService;
import demo.oauth2.client.service.front.FrontUserService;
import demo.oauth2.client.service.oauth2.Oauth2TokenService;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private FrontUserService frontUserService;
    @Autowired
    private Oauth2TokenService oauth2TokenService;

	@Override
	public String getTokenByCode(String code, String state) {
		//从认证中心获取token
		//它必须是具有code了，否则这种模式获取不了token
		Map<String, String> params = new HashMap<>();
		params.put("client_id", "1");
		params.put("client_secret", "111");
		params.put("grant_type", "authorization_code");
		params.put("redirect_uri", "http://localhost:8001/getToken");
		params.put("code", code);
		params.put("state", state);
		try {
			String urlBase = HttpUtil.getOauth2ServerBaseUrl();
			String result = HttpUtil.post(urlBase + "/oauth2/token.html", params);

			return afterGetToken(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getTokenByRefreshToken(String token) {
		//从认证中心刷新token
		//只需要refresh_token就可以，另必须是token本身已过期的情况
		Map<String, String> params = new HashMap<>();
		params.put("refresh_token", token);
		params.put("client_id", "1");
		params.put("client_secret", "111");
		params.put("grant_type", "refresh_token");
		try {
			String urlBase = HttpUtil.getOauth2ServerBaseUrl();
			String result = HttpUtil.post(urlBase + "/oauth2/token.html", params);

			return afterGetToken(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getTokenByClient(String token) {
		//客户端模式
		Map<String, String> params = new HashMap<>();
		params.put("client_id", "1");
		params.put("client_secret", "111");
		params.put("grant_type", "client");
		try {
			String urlBase = HttpUtil.getOauth2ServerBaseUrl();
			String result = HttpUtil.post(urlBase + "/oauth2/token.html", params);
			
			return afterGetToken(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getResourceRealname(String token) {
		//向资源服务器请求资源，这里是请求用户信息
		//获取到用户信息后，可以立即保存并关联已有用户，这就是微信登录的一般原理了。
		String usedToken = token;
		Oauth2Token oauth2Token = oauth2TokenService.selectByAccessToken(usedToken);
		
		if(null == oauth2Token) {
			throw new RuntimeException("unauthrization, please login before.");
		} else if(oauth2TokenService.checkExpireIn(oauth2Token.getExpireIn().getTime())){
			usedToken = getTokenByRefreshToken(oauth2Token.getRefreshToken());
		}
		
		try {
			String urlBase = HttpUtil.getResourceServerBaseUrl();
			String result = HttpUtil.get(urlBase + "/oauth2/api/userinfo.html?access_token=" + usedToken);
			JSONObject parseObject = JSONObject.parseObject(result);

			String code = parseObject.getString("err_code");
			if(!"200".equals(code)) {
				throw new RuntimeException("resource server error：" + parseObject.getString("err_msg"));
			}
			String userMsg = parseObject.getString("err_msg");
			
			updateTokenByRes(usedToken, userMsg);
			//如需关联用户，以username获取
			updateUserByRes(usedToken, userMsg);
			
			return userMsg;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String afterGetToken(String result) {
		//获取到token后，必须保存下来，免得每次都得远程请求。
		JSONObject parseObject = JSONObject.parseObject(result);
		if(!"200".equals(parseObject.getString("err_code"))) {
			throw new RuntimeException(parseObject.getString("err_msg"));
		}
		Oauth2Token token = new Oauth2Token();
		
		token.setAccessToken(parseObject.getString("access_token"));
		token.setRefreshToken(parseObject.getString("refresh_token"));
		token.setScope(parseObject.getString("scope"));
		token.setClientId("1");
		
		int expire_in = parseObject.getIntValue("expire_in");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, expire_in);
		token.setExpireIn(cal.getTime());
		
		oauth2TokenService.save(token);
		return token.getAccessToken();
	}

	@Override
	public void updateTokenByRes(String token, String result) {
		JSONObject userInfo = JSONObject.parseObject(result);
		String username = userInfo.getString("username");
		
		//
		Oauth2Token usedTokenObj = oauth2TokenService.selectByAccessToken(token);
		usedTokenObj.setUsername(username);
		oauth2TokenService.save(usedTokenObj);
	}

	@Override
	public void updateUserByRes(String token, String result) {
		JSONObject userInfo = JSONObject.parseObject(result);
		String username = userInfo.getString("username");
		String realname = userInfo.getString("realname");
		String nickname = userInfo.getString("nickname");
		
		//如需关联用户，以username获取
		FrontUser user = frontUserService.selectByUsername(username);
		if(user == null) {
			user = new FrontUser();
			
			user.setCreateTime(new Date());
			user.setDelFlag(0);
			user.setStatus(1);
		}
		user.setRealname(realname);
		user.setNickname(nickname);
		frontUserService.save(user);
	}
}