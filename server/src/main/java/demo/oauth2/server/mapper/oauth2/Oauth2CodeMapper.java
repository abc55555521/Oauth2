package demo.oauth2.server.mapper.oauth2;

import java.util.List;

import org.springframework.stereotype.Service;

import demo.oauth2.server.CustomMapper;
import demo.oauth2.server.model.oauth2.Oauth2Code;

@Service
public interface Oauth2CodeMapper extends CustomMapper<Oauth2Code>{
	List<Oauth2Code> list(Oauth2Code code);
	Oauth2Code selectById(Integer id);
	Oauth2Code selectByAuthCode(String authCode, String clientId);
	Oauth2Code selectByUsernameClientId(String username, String clientId);
}