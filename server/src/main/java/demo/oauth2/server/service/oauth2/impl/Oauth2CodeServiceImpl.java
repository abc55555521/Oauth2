package demo.oauth2.server.service.oauth2.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.oauth2.server.mapper.oauth2.Oauth2CodeMapper;
import demo.oauth2.server.model.oauth2.Oauth2Code;
import demo.oauth2.server.service.oauth2.Oauth2CodeService;

@Service
public class Oauth2CodeServiceImpl implements Oauth2CodeService {
	@Autowired
	private Oauth2CodeMapper oauth2CodeMapper;

	@Override
	public Oauth2Code selectByAuthCode(String authCode, String clientId) {
		return oauth2CodeMapper.selectByAuthCode(authCode, clientId);
	}

	@Override
	public Oauth2Code selectByUsernameClientId(String username, String clientId) {
		return oauth2CodeMapper.selectByUsernameClientId(username, clientId);
	}
	
	@Override
	public void save(Oauth2Code code) {
		if(code.getModelId() == null) {
			oauth2CodeMapper.insert(code);
		} else {
			oauth2CodeMapper.updateByPrimaryKey(code);
		}
	}

	@Override
	public void deleteById(Integer id) {
		oauth2CodeMapper.deleteByPrimaryKey(id);
	}
}