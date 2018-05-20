package demo.oauth2.client.service.front;

public interface CommonService {
	String getTokenByCode(String code, String state);
	String getTokenByRefreshToken(String token);
	String getTokenByClient(String token);
	String getResourceRealname(String token);
	
	void updateTokenByRes(String token, String result);
	void updateUserByRes(String token, String result);
	
	String afterGetToken(String result);
}