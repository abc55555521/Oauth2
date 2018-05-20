package demo.oauth2.resource.model.front;

import java.io.Serializable;
import java.util.Date;

import demo.oauth2.resource.model.BaseEntity;

public class FrontUser extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1854627726948702484L;
	
	private String username;
	private String nickname;
	private String realname;
	private String password;
	private String email;
	private String phone;
	private String address;
	private String zip;
	private Date createTime;
	private Integer title;
	private Integer delFlag;
	private Integer status;
	private String sort;

	public FrontUser() {
	}

	public FrontUser(Integer modelId, String username, String realname) {
		this.modelId = modelId;
		this.username = username;
		this.realname = realname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String aValue) {
		username = aValue;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String aValue) {
		password = aValue;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String aValue) {
		email = aValue;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String aValue) {
		phone = aValue;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String aValue) {
		address = aValue;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String aValue) {
		zip = aValue;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer aValue) {
		status = aValue;
	}

	public Integer getTitle() {
		return title;
	}

	public void setTitle(Integer aValue) {
		title = aValue;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return status.shortValue() == 1;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

}