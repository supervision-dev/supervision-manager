package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.DataListResult;
import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.RoleService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;


@Scope("prototype")
@Controller
@RequestMapping("/system/user")
public class UserController extends SystemAction {

	/**
	 * 资源注入
	 */
	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;
	@Resource
	private OrganService organService; 		
	@Resource
	private ConfigService configService; 
	@Resource
	private SysLogService logService;
	
	/**
     * 用户管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @ResponseBody
    @RequestMapping(value = "/userList.do")
//    @RequiresPermissions("system/user/userList.do")
    public DataListResult<User> userList(User user, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	DataListResult<User> dr = new DataListResult<User>();
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(user.getSearchName() != null && user.getSearchName() != ""){
			String searchName =  URLDecoder
					.decode(user.getSearchName(), "utf-8");
			user.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (user.getPageNo() == null)
			user.setPageNo(1);
		user.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;
		//分页集合
		List<User> userList = new ArrayList<User>();
		//获取当前登录用户
    	User lgUser = this.getLoginUser();
    	List<Organ> userOrgList=userService.getUserOrgByUserId(lgUser.getId());
    	List<Role> rolesByUserId = roleService.getRolesByUserId(lgUser.getId());
    	Role role = rolesByUserId.get(0);
//    	List<Organ> userOrgList=userService.getUserOrgByUserId(2);
    	//判断当前登录账号是不是超级管理员
		if(lgUser.getAccount().equals(Constants.USER_SUPER_ADMIN_ACCOUNT) 
				||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_1 
				||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_2
				||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_3
				||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_4){
			try{
				//t_user取满足要求的参数数据
				userList =  userService.getUserList(user);					
				//t_user取满足要求的记录总数
				totalCount = userService.getUserCount(user);
			}catch(Exception ex){ 
				ex.printStackTrace();
			}	
		}else {
			try{
				lgUser.setSearchName(user.getSearchName()); 
				//如果当前登录机构是中支机构，则获取该中支下的所有部门和县支行
				if(userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_6){
					/*Organ organ = userOrgList.get(0);
					List<Organ> organByPId = organService.getOrganByPId(organ.getId());
					if(user.getSearchName() != null && user.getSearchName() != ""){
						List<User> searchUserList = new ArrayList<User>();
						for (Organ organ2 : organByPId) {
								List<User> userListByOrgId = userService.getUserListByOrgId(organ2.getId());
								searchUserList.addAll(userListByOrgId);
						}
						for (User user2 : searchUserList) {
							user2.setSearchName(user.getSearchName());
							User searchUser= userService.getUserBySearchName(user2);
							if(searchUser!=null){
								userList.add(searchUser);
							}
						}
						totalCount+=userList.size();
					}else {
						for (Organ organ2 : organByPId) {
							List<User> userListByOrgId = userService.getUserListByOrgId(organ2.getId());
							userList.addAll(userListByOrgId);
							totalCount+=userListByOrgId.size();
						}
					}*/
					Organ organ = userOrgList.get(0);
					List<Integer> orgIdList=organService.getOrganIdByPid(organ.getId());
					Map<String, Object> map= new HashMap<String, Object>();
					map.put("orgIdList", orgIdList);
					map.put("user", user);
					userList= userService.getUserByOrgidsAndSearchUser(map);
					totalCount = userService.getUserCountByOrgidsAndSearchUser(map);
				}
				//获取当前登录用户所属的机构ID
				List<Integer> userOrgIds=userService.getUserOrgIdsByUserId(lgUser.getId());
				//将用户所属的机构id存入到session中
				HttpSession session = request.getSession();
				session.setAttribute("userOrgIds", userOrgIds); 
				
				//根据机构ID查询用户
				/*List<User> userByOrgids = userService.getUserByOrgids(userOrgIds);					
				//int countByOrgId = userService.getUserCountByOrgId(lgUser);
				userList.addAll(userByOrgids);
				totalCount+=userByOrgids.size();*/
			}catch(Exception ex){ 
				ex.printStackTrace();
			}	 
		}
		user.setTotalCount(totalCount); 	
		user.setRoleId(role.getId());
		dr.setData(user);
		dr.setDatalist(userList); 
    	return dr;
    }
    
    /**
     * 用户角色列表获取
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @ResponseBody
    @RequestMapping(value = "/getRoleList.do")
//    @RequiresPermissions("system/user/getRoleList.do")
    public List<Role> getRoleList(HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException { 
    	List<Role>  list = new ArrayList<Role>();
    	User lgUser = this.getLoginUser();
    	List<Role> rolesByUserId = roleService.getRolesByUserId(lgUser.getId());
    	Role role = rolesByUserId.get(0);
    	if(role.getId()==11){
    		list = roleService.getRoleListByUsed(new Role());
    	}else{
    		list = roleService.getRoleList(new Role());
		}
    	/*if(list != null){
	    	Collections.sort(list, new Comparator<Role>() {
	            public int compare(Role arg0, Role arg1) {
	                return arg0.getName().compareTo(arg1.getName());
	            }
	        });
    	}*/
    	return list;
    }
    
    /**
     * 用户职位列表获取
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @ResponseBody
    @RequestMapping(value = "/getPostList.do")
//    @RequiresPermissions("system/user/getPostList.do")
    public List<Meta> getPostList(HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException { 
    	List<Meta>  list = configService.getMeatListByKey(Constants.META_POSITION_KEY);
    	if(list != null){
	    	Collections.sort(list, new Comparator<Meta>() {
	            public int compare(Meta arg0, Meta arg1) {
	                return arg0.getName().compareTo(arg1.getName());
	            }
	        });
    	}
    	return list;
    }

    /** 
	 * 新增,编辑用户
	 */
    @ResponseBody
    @RequestMapping(value = "/jsonSaveOrUpdateUser.do", method=RequestMethod.POST)
    /*@RequiresPermissions("system/user/jsonSaveOrUpdateUser.do")*/
    public JsonResult<User> jsonSaveOrUpdateDevice(User user,
    		HttpServletRequest request, HttpServletResponse response){
    	
    	//新建一个json对象 并赋初值
		JsonResult<User> js = new JsonResult<User>();
		js.setCode(new Integer(1));
		js.setMessage("保存用户信息失败!");
		
		boolean State =  false;
		try {
			//如为新增，则给id置0
			if (user.getId() == null || user.getId() == 0) {
				user.setId(0);					
			} 
			user.setUsed(1);
			//创建用于新增时根据用户账号去查询用户是否存在的user对象
			User u = new User();
			u.setId(user.getId());
			u.setName(user.getName());
			u.setAccount(user.getAccount());
			List<Integer> roleIds = new ArrayList<Integer>();
			roleIds.add(user.getRoleId());
			List<Integer> orgIds = new ArrayList<Integer>();
			orgIds.add(user.getOrgId());
			Integer postId =  user.getPostId();
			//如为编辑，则给新建user对象赋传来的设备id值
			if (user.getId() > 0) {
				u.setId(user.getId());					
				State = userService.saveOrUpdateUser(user,roleIds,orgIds,postId);
				if(State){
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，执行了修改用户信息", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存用户信息成功!");
					return js;
				}else{
					return js;
				}
			}
			//根据id去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配该账号是否重复
			List<User> lc = userService.getExistUser(u);
			if (lc.size() == 0) {  
				State = userService.saveOrUpdateUser(user,roleIds,orgIds,postId);
				if(State){
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，新增了"+user.getName()+"用户", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存用户信息成功!");
					return js;
				}else{
					return js;
				}
			} else {
				js.setMessage("该用户账号已存在!");
				return js;
			} 
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return js;
    }

    /**
     * 删除用户
     */
    @ResponseBody
	@RequestMapping(value = "/jsondeleteUserById.do", method = RequestMethod.POST)
	/*@RequiresPermissions("system/user/jsondeleteUserById.do")*/
	public JsonResult<User> jsondeleteUserById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 新建一个json对象 并赋初值
		JsonResult<User> js = new JsonResult<User>();
		js.setCode(new Integer(1));
		js.setMessage("删除失败!");
		try {			
			User user = userService.getUserById(id);
			boolean state = userService.deleteUserById(id);				
			if(state){
				js.setCode(new Integer(0));
				js.setMessage("删除成功!");
				User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，删除了"+user.getName()+"用户", 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
				
				return js;
			}else {
				return js;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}			
		return js;
	}

    /**
     * 重置密码
     */
    @ResponseBody
	@RequestMapping(value = "/jsonResetUserPwd.do", method = RequestMethod.POST)
	/*@RequiresPermissions("system/user/jsonResetUserPwd.do")*/
	public JsonResult<User> jsonResetUserPwd(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
    	// 新建一个json对象 并赋初值
		JsonResult<User> js = new JsonResult<User>();
		js.setCode(new Integer(1));
		js.setMessage("重置用户密码失败!");		
    	
		//根据id查询用户
		User userById = userService.getUserById(id);
		//重置用户密码
		userById.setPwd(Constants.DEFAULT_USER_PASSWORD);
		userById.setIsChangePwd(null);
		userById.setFailNumber(0);
		userById.setIsLocking(0);
		userById.setLogonTime(null);
		try{
			boolean state = userService.updateByPrimaryKey(userById);
			if(state){
				User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，重置了用户："+userById.getName()+"的密码", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(new Integer(0));
				js.setMessage("重置用户密码成功!");
				return js;
			}else {
				return js;
			}				
		}catch (Exception e) {
			e.printStackTrace();
		}			
		return js;
    }

    /**
     * 根据Id获取用户主题信息
     */
    @ResponseBody
	@RequestMapping(value = "/jsonloadUserInfo.do", method = RequestMethod.POST)
	/*@RequiresPermissions("system/user/jsonloadUserInfo.do")*/
	public User jsonloadUserInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
    	User user = null;
		try{
			if(id != null && id >0){
				user = userService.getUserById(id);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}			
		return user;
    }

	/**
	 * 加载机构的树
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadOrganTreeList.do")
	public List<Organ> getOrganList(
			@RequestParam(value = "pid", required = false) Integer pid,
			HttpServletRequest request, HttpServletResponse response) {
		
		Organ organ = new Organ();
		Organ thisOrg = null;
		if (pid != null && pid>0) {
			organ.setPid(pid);
		} else {
			//获取当前登录用户
			User loginUser = this.getLoginUser();
			//获取当前用户对应的机构列表
			List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
			//获取当前用户对应的第一个机构
			Organ userOrg=userOrgList.get(0);
			if(userOrg.getOrgtype() == Constants.ORG_TYPE_4){//分行监察室
				organ.setPid(0);
			}else if(userOrg.getOrgtype() == Constants.ORG_TYPE_6){//中支机构 
				organ.setPid(userOrg.getId());
				thisOrg = organService.selectByPrimaryKey(userOrg.getId());
			}else{//中支监察室 
				organ.setPid(userOrg.getPid());
			}
		} 
		//获取用户所属的机构  
		List<Organ> list = new ArrayList<Organ>();
		if(thisOrg !=null){
			list.add(thisOrg);
		}else{
			 list = organService.getOrganByPId(organ);	
		}
		return list;// json.toString();
	}  
    
}
