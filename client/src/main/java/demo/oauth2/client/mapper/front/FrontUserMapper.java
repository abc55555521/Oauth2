package demo.oauth2.client.mapper.front;

import java.util.List;

import org.springframework.stereotype.Service;

import demo.oauth2.client.CustomMapper;
import demo.oauth2.client.model.front.FrontUser;

@Service
public interface FrontUserMapper extends CustomMapper<FrontUser>{
	List<FrontUser> list(FrontUser user);
	FrontUser selectById(Integer id);
	FrontUser selectByUsername(String clientId);
}