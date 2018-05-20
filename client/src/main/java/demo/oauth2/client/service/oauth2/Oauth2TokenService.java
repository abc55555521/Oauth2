package demo.oauth2.client.service.oauth2;

import demo.oauth2.client.model.oauth2.Oauth2Token;

public abstract interface Oauth2TokenService {
	Oauth2Token selectByAccessToken(String accessToken);
	Oauth2Token selectByRefreshToken(String refreshToken);
	Oauth2Token selectByUsernameClientId(String username, String clientId);
	boolean checkExpireIn(Long expireIn);
	void save(Oauth2Token token);
	void deleteById(Integer id);
}