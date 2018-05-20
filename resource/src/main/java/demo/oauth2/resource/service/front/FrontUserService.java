package demo.oauth2.resource.service.front;

import demo.oauth2.resource.model.front.FrontUser;

public interface FrontUserService {
	FrontUser selectById(Integer id);
	FrontUser selectByUsername(String username);
	void save(FrontUser user);
	void deleteById(Integer id);
}