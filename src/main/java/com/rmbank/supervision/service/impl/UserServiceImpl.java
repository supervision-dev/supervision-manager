package com.rmbank.supervision.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import com.rmbank.supervision.common.ReturnResult;
import com.rmbank.supervision.common.shiro.ShiroUsernamePasswordToken;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.EndecryptUtils;
import com.rmbank.supervision.dao.OrganMapper;
import com.rmbank.supervision.dao.UserMapper;
import com.rmbank.supervision.dao.UserOrganMapper;
import com.rmbank.supervision.dao.UserRoleMapper;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.model.UserOrgan;
import com.rmbank.supervision.model.UserRole;
import com.rmbank.supervision.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;
	@Resource
	private UserRoleMapper userRoleMapper;
	@Resource
	private UserOrganMapper userOrganMapper;
	@Resource
	private OrganMapper organMapper;

	@Override
	public ReturnResult<User> login(String name, String pwd, boolean rememberMe) {
		Subject subject = SecurityUtils.getSubject();
		ReturnResult<User> res = new ReturnResult<User>();
		try {
			User temp = new User();
			temp.setAccount(name);
			temp.setUsed(Constants.USER_STATUS_EFFICTIVE);
			User u = this.userMapper.getUserByAccount(temp);
			if (u == null) {
				res.setCode(Integer.valueOf(0));
				res.setMessage("用户[" + name + "]不存在！");
			} else {
				// 该账号不为空，判断是否锁定
				if (u.getIsLocking() == 0) {// 等于0代表没有被锁定
					ShiroUsernamePasswordToken token = new ShiroUsernamePasswordToken(
							u.getAccount(), pwd, u.getPwd(), u.getSalt(), null);
					// token.setRememberMe(rememberMe);
					subject.login(token);
					boolean state = subject.isAuthenticated();
					if (subject.isAuthenticated()) {
						u.setLogonTime(null);
						u.setFailNumber(0);
						u.setIsLocking(0);
						userMapper.updateByPrimaryKey(u);
						
						res.setCode(Integer.valueOf(1));
						res.setMessage("登录成功！");
						res.setResultObject(u);
					} else {
						res.setCode(Integer.valueOf(0));
						res.setMessage("登录失败，密码错误。");

						User user = new User();
						user.setAccount(name);
						User userByAccount = userMapper.getUserByAccount(user);
						if(userByAccount.getFailNumber()<=4){
							userByAccount.setLogonTime(new Date());
							userMapper.updateByAccount(userByAccount);
							res.setCode(Integer.valueOf(0));
							res.setMessage("登录失败，密码错误。");
						}else {
							userByAccount.setLogonTime(new Date());
							userByAccount.setIsLocking(1);
							userMapper.updateByAccount(userByAccount);
							res.setCode(Integer.valueOf(0));
							res.setMessage("你已连续5次登陆失败，该账号已被锁定，请在15分钟后再登陆");
						}
					}
				} else if (u.getIsLocking() == 1) {// 等于1代表该账号已被锁定
					long l = new Date().getTime() - u.getLogonTime().getTime();
					long day = l / (24 * 60 * 60 * 1000);
					long hour = (l / (60 * 60 * 1000) - day * 24);
					long min = 15 - ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
					if(min<=0){
						min=1;
					}
					res.setCode(Integer.valueOf(0));
					res.setMessage("该账号已被锁定，请在" + min + "分钟后登陆");
				}
			}

		} catch (ExcessiveAttemptsException ex) {
			res.setCode(Integer.valueOf(0));
			res.setMessage("登录失败，未知错误。");
		} catch (AuthenticationException ex) {
			User user = new User();
			user.setAccount(name);
			User userByAccount = userMapper.getUserByAccount(user);
			if(userByAccount.getFailNumber()<4){
				userByAccount.setLogonTime(new Date());
				userMapper.updateByAccount(userByAccount);
				res.setCode(Integer.valueOf(0));
				res.setMessage("登录失败，密码错误！您还有"+(4-userByAccount.getFailNumber())+"次机会");
			}else {
				userByAccount.setLogonTime(new Date());
				userByAccount.setIsLocking(1);
				userMapper.updateByAccount(userByAccount);
				res.setCode(Integer.valueOf(0));
				res.setMessage("你已连续5次登陆失败，该账号已被锁定，请在15分钟后再登陆");
			}
		}
		return res;
	}

	@Override
	public User getUserByAccount(String username) {
		// TODO Auto-generated method stub
		User user = new User();
		user.setAccount(username);
		return userMapper.getUserByAccount(user);
	}

	/**
	 * 获取用户列表
	 */
	@Override
	public List<User> getUserList(User user) {
		// TODO Auto-generated method stub
		return userMapper.getUserList(user);
	}

	/**
	 * 获取用户记录数
	 */
	@Override
	public int getUserCount(User user) {
		// TODO Auto-generated method stub
		return userMapper.getUserCount(user);
	}

	/**
	 * 编辑用户，根据id回显用户信息
	 */
	@Override
	public User getUserById(Integer id) {
		// TODO Auto-generated method stub
		return userMapper.getUserById(id);
	}

	/**
	 * 新增用户时查询用户名是否存在
	 */
	@Override
	public List<User> getExistUser(User u) {
		// TODO Auto-generated method stub
		return userMapper.getExistUser(u);
	}

	/**
	 * 新增用户/修改用户
	 */
	@Override
	public boolean saveOrUpdateUser(User user, List<Integer> roleIds,
			List<Integer> orgIds, Integer postId) {
		boolean isSuccess = false;
		try {
			// id存在则为修改操作
			if (user.getId() > 0) {
				// 修改用户的数据
				userMapper.updateByPrimaryKeySelective(user);
				// 根据用户id删除用户-机构表中用户id对应的数据
				userOrganMapper.deleteByUserId(user.getId());
				// 根据用户id删除用户-角色表中用户id对应的数据
				userRoleMapper.deleteByUserId(user.getId());
				for (Integer roleId : roleIds) {
					UserRole userRole = new UserRole();
					userRole.setId(0);
					userRole.setUserId(user.getId());
					userRole.setRoleId(roleId);
					userRoleMapper.insert(userRole);
				}
				for (Integer orgId : orgIds) {
					UserOrgan userOrg = new UserOrgan();
					userOrg.setId(0);
					userOrg.setUserId(user.getId());
					userOrg.setOrgId(orgId);
					userOrg.setPostId(postId);
					userOrganMapper.insert(userOrg);
				}

				isSuccess = true;
			} else {// 新增用户
					// insert返回用户主键
				int userId = 0;
				User u = EndecryptUtils.md5Password(user.getAccount(),
						user.getPwd());
				if (u != null) {
					user.setPwd(u.getPwd());
					user.setSalt(u.getSalt());
					userMapper.insert(user);
					userId = user.getId();
					System.out.println("返回的userID" + userId);
				}
				if (userId != 0) {
					for (Integer roleId : roleIds) {
						UserRole userRole = new UserRole();
						userRole.setId(0);
						userRole.setUserId(userId);
						userRole.setRoleId(roleId);
						userRoleMapper.insert(userRole);
					}
					for (Integer orgId : orgIds) {
						UserOrgan userOrg = new UserOrgan();
						userOrg.setId(0);
						userOrg.setUserId(userId);
						userOrg.setOrgId(orgId);
						userOrg.setPostId(postId);
						userOrganMapper.insert(userOrg);
					}
					isSuccess = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;

	}

	/**
	 * 删除用户
	 */
	@Override
	public boolean deleteUserById(Integer id) {
		boolean isSuccess = false;
		try {
			userMapper.deleteUserById(id);
			// 根据用户id删除用户-机构表中用户id对应的数据
			userOrganMapper.deleteByUserId(id);
			// 根据用户id删除用户-角色表中用户id对应的数据
			userRoleMapper.deleteByUserId(id);
			isSuccess = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * 修改用户状态
	 */
	@Override
	public boolean updateUserUsedById(User user) {
		boolean isSuccess = false;
		try {
			userMapper.updateUserUsedById(user);
			isSuccess = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * 重置用户密码
	 */
	@Override
	public boolean updateByPrimaryKey(User user) {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
		try {
			User u = EndecryptUtils.md5Password(user.getAccount(),
					user.getPwd());
			user.setPwd(u.getPwd());
			user.setSalt(u.getSalt());
			userMapper.updateByPrimaryKey(user);
			isSuccess = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public List<User> getUserListByOrgId(User lgUser) {
		// TODO Auto-generated method stub
		return userMapper.getUserListByOrgId(lgUser);
	}

	@Override
	public int getUserCountByOrgId(User lgUser) {
		// TODO Auto-generated method stub
		return userMapper.getUserCountByOrgId(lgUser);
	}

	/**
	 * 根据用户ID查询所属的机构ID
	 */
	@Override
	public List<Integer> getUserOrgIdsByUserId(Integer id) {
		// TODO Auto-generated method stub
		return userOrganMapper.getUserOrgIdsByUserId(id);
	}

	/**
	 * 查询用户对应机构下的子机构
	 * 
	 */
	@Override
	public List<Integer> getUserOrgIdsByList(List<Integer> userOrgIds) {
		// TODO Auto-generated method stub
		return organMapper.getUserOrgIdsByList(userOrgIds);
	}

	@Override
	public List<User> getUserByOrgids(List<Integer> userOrgIds) {
		// TODO Auto-generated method stub
		return userMapper.getUserByOrgids(userOrgIds);
	}

	@Override
	public List<Organ> getUserOrgByList(List<Integer> userOrgIds) {
		// TODO Auto-generated method stub
		return organMapper.getUserOrgByList(userOrgIds);
	}

	/**
	 * 点击机构树时根据机构ID查询对应的用户
	 */
	@Override
	public List<User> getUserListByOrgId(Integer orgId) {
		// TODO Auto-generated method stub
		return userMapper.getUserListByOrgId(orgId);
	}

	@Override
	public List<Organ> getUserOrgByUserId(Integer id) {
		// TODO Auto-generated method stub
		return organMapper.getUserOrgByUserId(id);
	}

	/**
	 * 获取当前登录用户的所属机构下的所有用户
	 */
	@Override
	public List<User> getUserListByLgUser(User lgUser) {
		// TODO Auto-generated method stub
		return userMapper.getUserListByLgUser(lgUser);
	}

	@Override
	public void updateByPrimaryKeySelective(User user) {
		// TODO Auto-generated method stub
		userMapper.updateByPrimaryKeySelective(user);
	}

	@Override
	public int updateByAccount(User user) {
		// TODO Auto-generated method stub
		return userMapper.updateByAccount(user);
	}

	@Override
	public List<User> getUserByisLocking(User user) {
		// TODO Auto-generated method stub
		return userMapper.getUserByisLocking(user);
	}

	@Override
	public void resetPwd(User user) {
		// TODO Auto-generated method stub
		try {
			User u = EndecryptUtils.md5Password(user.getAccount(),user.getPwd());	
			user.setPwd(u.getPwd());
			user.setSalt(u.getSalt());
			user.setIsChangePwd(null);
			user.setFailNumber(0);
			user.setIsLocking(0);
			user.setLogonTime(null);
			userMapper.resetPwd(user);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
