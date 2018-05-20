package demo.oauth2.server.model.oauth2;

import java.io.Serializable;
import java.sql.Timestamp;

import demo.oauth2.server.model.BaseEntity;

public class Oauth2Code extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6241688662001224995L;
	private String authCode;
	private String username;
	private String clientId;
	private Timestamp expireIn;

	public String getAuthCode() {
        return authCode;
    }
    
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public Timestamp getExpireIn() {
        return expireIn;
    }
    
    public void setExpireIn(Timestamp expireIn) {
        this.expireIn = expireIn;
    }
}