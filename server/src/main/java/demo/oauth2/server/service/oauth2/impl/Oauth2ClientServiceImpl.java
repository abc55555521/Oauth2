package demo.oauth2.server.service.oauth2.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.oauth2.server.mapper.oauth2.Oauth2ClientMapper;
import demo.oauth2.server.model.oauth2.Oauth2Client;
import demo.oauth2.server.service.oauth2.Oauth2ClientService;

@Service("oauth2ClientService")
public class Oauth2ClientServiceImpl implements Oauth2ClientService {

    @Autowired
    private Oauth2ClientMapper oauth2ClientMapper;

	@Override
	public boolean checkClientSecret(Oauth2Client client, String clientSecret) {
		if(client == null) return false;
		
		try {
			if(client.getClientSecret().equals(clientSecret)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean checkExpireIn(long expireIn) {
		//时间戳超期未
    	long timeStamp = new Date().getTime() / 1000L;
        return timeStamp < expireIn;
	}

	@Override
	public long getExpireIn() {
		return 3600L;
	}

	@Override
	public Oauth2Client selectByClientId(String clientId) {
		return oauth2ClientMapper.selectByClientId(clientId);
	}

	@Override
	public void save(Oauth2Client client) {
		if(client.getModelId() == null) {
			oauth2ClientMapper.insert(client);
		} else {
			oauth2ClientMapper.updateByPrimaryKey(client);
		}
	}

	@Override
	public void deleteById(Integer id) {
		oauth2ClientMapper.deleteByPrimaryKey(id);
	}
}