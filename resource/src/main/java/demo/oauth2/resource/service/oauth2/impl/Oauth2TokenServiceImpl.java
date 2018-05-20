package demo.oauth2.resource.service.oauth2.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.oauth2.resource.mapper.oauth2.Oauth2TokenMapper;
import demo.oauth2.resource.model.oauth2.Oauth2Token;
import demo.oauth2.resource.service.oauth2.Oauth2TokenService;

@Service
public class Oauth2TokenServiceImpl implements Oauth2TokenService {
	@Autowired
	private Oauth2TokenMapper oauth2TokenMapper;

	@Override
	public Oauth2Token selectByAccessToken(String accessToken) {
		return oauth2TokenMapper.selectByAccessToken(accessToken);
	}

	@Override
	public Oauth2Token selectByRefreshToken(String refreshToken) {
		return oauth2TokenMapper.selectByRefreshToken(refreshToken);
	}

	@Override
	public Oauth2Token selectByUsernameClientId(String username, String clientId) {
		return oauth2TokenMapper.selectByUsernameClientId(username, clientId);
	}

	@Override
	public boolean checkExpireIn(Long expireIn) {
		//时间戳超期未，因存储的过期时间是计算好的指定日期，因此，在这里仅判断是否过了时间点
    	long timeStamp = new Date().getTime();
        return expireIn == null || timeStamp > expireIn.longValue();
	}
	@Override
	public void save(Oauth2Token token) {
		if(token.getModelId() == null) {
			oauth2TokenMapper.insert(token);
		} else {
			oauth2TokenMapper.updateByPrimaryKey(token);
		}
	}

	@Override
	public void deleteById(Integer id) {
		oauth2TokenMapper.deleteByPrimaryKey(id);
	}
}