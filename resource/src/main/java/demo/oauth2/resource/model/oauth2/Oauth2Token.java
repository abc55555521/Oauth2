package demo.oauth2.resource.model.oauth2;

import java.io.Serializable;
import java.util.Date;

import demo.oauth2.resource.model.BaseEntity;

public class Oauth2Token extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8791708083663730280L;
	private String accessToken;
	private String username;
	
	private Date expireIn;
	private String scope;

	public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public Date getExpireIn() {
        return expireIn;
    }
    
    public void setExpireIn(Date expireIn) {
        this.expireIn = expireIn;
    }
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}