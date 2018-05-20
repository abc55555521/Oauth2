package demo.oauth2.resource.service.front.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.oauth2.resource.mapper.front.FrontUserMapper;
import demo.oauth2.resource.model.front.FrontUser;
import demo.oauth2.resource.service.front.FrontUserService;

@Service
public class FrontUserServiceImpl implements FrontUserService {

    @Autowired
    private FrontUserMapper frontUserMapper;

	@Override
	public FrontUser selectById(Integer id) {
		return frontUserMapper.selectById(id);
	}

	@Override
	public FrontUser selectByUsername(String username) {
		return frontUserMapper.selectByUsername(username);
	}

	@Override
	public void save(FrontUser user) {
		if(user.getModelId() == null) {
			frontUserMapper.insert(user);
		} else {
			frontUserMapper.updateByPrimaryKey(user);
		}
	}

	@Override
	public void deleteById(Integer id) {
		frontUserMapper.deleteByPrimaryKey(id);
	}
}