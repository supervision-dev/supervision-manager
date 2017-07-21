package com.rmbank.test;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.rmbank.supervision.dao.UserMapper;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.UserService;

public class ResetPwd {

	@Resource
	private UserMapper userMapper;
	@Resource
	private UserService userService;
	
	@Test
	public void test() {
		List<User> userAll = userMapper.getUserAll();
		System.out.println("用户数为："+userAll.size());
		/*for (User user : userAll) {
			userService.resetPwd(user);
		}*/
	}

}
