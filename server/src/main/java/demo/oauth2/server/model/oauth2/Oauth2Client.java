package demo.oauth2.server.model.oauth2;

import java.io.Serializable;

import demo.oauth2.server.model.BaseEntity;

public class Oauth2Client extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8589845806094923771L;
	private String clientName;
	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String clientUri;
	private String clientIconUri;
	private String resourceIds;
	private String scope;
	private String grantTypes;
	private String allowedIps;
	private Integer status;
	private Integer trusted;

	public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    public String getClientUri() {
        return clientUri;
    }
    
    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }
    public String getClientIconUri() {
        return clientIconUri;
    }
    
    public void setClientIconUri(String clientIconUri) {
        this.clientIconUri = clientIconUri;
    }
    public String getResourceIds() {
        return resourceIds;
    }
    
    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getGrantTypes() {
        return grantTypes;
    }
    
    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }
    public String getAllowedIps() {
        return allowedIps;
    }
    
    public void setAllowedIps(String allowedIps) {
        this.allowedIps = allowedIps;
    }
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getTrusted() {
        return trusted;
    }
    
    public void setTrusted(Integer trusted) {
        this.trusted = trusted;
    }
}