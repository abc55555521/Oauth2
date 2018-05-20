package demo.oauth2.server.service.oauth2;

import demo.oauth2.server.model.oauth2.Oauth2Code;

public abstract interface Oauth2CodeService {
	Oauth2Code selectByAuthCode(String authCode, String clientId);
	Oauth2Code selectByUsernameClientId(String username, String clientId);
	void save(Oauth2Code code);
	void deleteById(Integer id);
}