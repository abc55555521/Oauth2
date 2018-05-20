package demo.oauth2.server.model.oauth2;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import demo.oauth2.server.model.BaseEntity;

public class Oauth2Token extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8791708083663730280L;
	private String accessToken;
	private String refreshToken;
	private String username;
	private String clientId;
	private Timestamp expireIn;
	private Timestamp refreshExpireIn;
	private String scope;

	public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
    public Date getExpireIn() {
        return expireIn;
    }
    
    public void setExpireIn(Timestamp expireIn) {
        this.expireIn = expireIn;
    }
    public Date getRefreshExpireIn() {
        return refreshExpireIn;
    }
    
    public void setRefreshExpireIn(Timestamp refreshExpireIn) {
        this.refreshExpireIn = refreshExpireIn;
    }
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
}