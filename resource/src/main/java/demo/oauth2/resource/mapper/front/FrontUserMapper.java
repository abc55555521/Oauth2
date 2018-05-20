package demo.oauth2.resource.mapper.front;

import java.util.List;

import org.springframework.stereotype.Service;

import demo.oauth2.resource.CustomMapper;
import demo.oauth2.resource.model.front.FrontUser;

@Service
public interface FrontUserMapper extends CustomMapper<FrontUser>{
	List<FrontUser> list(FrontUser user);
	FrontUser selectById(Integer id);
	FrontUser selectByUsername(String clientId);
}