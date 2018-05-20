package demo.oauth2.resource.mapper.oauth2;

import java.util.List;

import org.springframework.stereotype.Service;

import demo.oauth2.resource.CustomMapper;
import demo.oauth2.resource.model.oauth2.Oauth2Token;

@Service
public interface Oauth2TokenMapper extends CustomMapper<Oauth2Token>{
	List<Oauth2Token> list(Oauth2Token token);
	Oauth2Token selectById(Integer id);
	Oauth2Token selectByAccessToken(String accessToken);
	Oauth2Token selectByRefreshToken(String refreshToken);
	Oauth2Token selectByUsernameClientId(String username, String clientId);
}