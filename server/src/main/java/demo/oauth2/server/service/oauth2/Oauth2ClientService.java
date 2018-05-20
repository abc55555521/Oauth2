package demo.oauth2.server.service.oauth2;

import demo.oauth2.server.model.oauth2.Oauth2Client;

public interface Oauth2ClientService {
	boolean checkClientSecret(Oauth2Client client, String clientSecret);

	boolean checkExpireIn(long expireIn);

	long getExpireIn();
	
	Oauth2Client selectByClientId(String clientId);
	
	void save(Oauth2Client client);
	void deleteById(Integer id);
}