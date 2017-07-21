package com.rmbank.test;

import static org.junit.Assert.*;

import java.util.List;






import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration 
@ContextConfiguration(locations = { "classpath:/spring/spring-*.xml"})
@Transactional
public class ResetPwd extends TestCase  {
	
	@Resource
	private UserService userService;
	
	@Test
	public void test() {
		List<User> userAll = userService.getUserAll();
		System.out.println("用户数为："+userAll.size());
		/*for (User user : userAll) {
			userService.resetPwd(user);
		}*/
	}

}
