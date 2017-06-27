package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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

import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.model.UserOrgan;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.RoleService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;
import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;


/*@Scope("prototype")
@Controller
@RequestMapping("/system/user")*/
public class UserAction extends SystemAction  {
	
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
	    @RequestMapping(value = "/userList.do")
	    @RequiresPermissions("system/user/userList.do")
	    public String userList(User user, 
	            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
	    	
	    	//判断搜索名是否为空，不为空则转为utf-8编码 		
			if(user.getSearchName() != null && user.getSearchName() != ""){
				String searchName =  new String(user.getSearchName().getBytes(
						"iso8859-1"), "utf-8");
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
	    	//判断当前登录账号是不是超级管理员
			if(lgUser.getAccount().equals(Constants.USER_SUPER_ADMIN_ACCOUNT) 
					|| userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_1 
					||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_2
					||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_3){
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
					//t_user取满足要求的参数数据
					/*userList = userService.getUserListByOrgId(lgUser);					
					//t_user取满足要求的记录总数
					totalCount = userService.getUserCountByOrgId(lgUser);*/
					
					//获取当前登录用户所属的机构ID
					List<Integer> userOrgIds=userService.getUserOrgIdsByUserId(lgUser.getId());
					//将用户所属的机构id存入到session中
					HttpSession session = request.getSession();
					session.setAttribute("userOrgIds", userOrgIds);
					
					//根据机构ID查询用户
					userList=userService.getUserByOrgids(userOrgIds);					
					totalCount = userService.getUserCountByOrgId(lgUser);

					//查询用户对应机构下的下一级机构
					//List<Integer> childrenOrgid=userService.getUserOrgIdsByList(userOrgIds);
					//List<Organ> childrenOrg=userService.getUserOrgByList(userOrgIds);
					//userOrgIds.addAll(childrenOrgid);					

				}catch(Exception ex){ 
					ex.printStackTrace();
				}	
			}
			
			user.setTotalCount(totalCount); 	
			//通过request对象传值到前台
			request.setAttribute("User", user);			
	    	request.setAttribute("userList", userList);
	    	
	    	//获取用户对应的职务
//	    	List<Meta> postList=configService.getUserPost();
//	    	request.setAttribute("postList", postList);
	    	
	    	
	    	User loginUser = this.getLoginUser();
			String ip = IpUtil.getIpAddress(request);		
			logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了用户列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
	    	return "web/base/user/userList";
	    }
	    
	    
    	 /**
	     * 根据机构ID查询用户
	     */
	    @ResponseBody
		@RequestMapping(value = "/jsonLoadUserListByOrgId.do", method = RequestMethod.POST)
		@RequiresPermissions("system/user/jsonLoadUserListByOrgId.do")
		public JsonResult<User> jsonLoadUserListByOrgId(
				@RequestParam(value = "orgId", required = false) Integer orgId,
				HttpServletRequest request, HttpServletResponse response) {
			
			// 新建一个json对象 并赋初值
			JsonResult<User> js = new JsonResult<User>();
	        js.setCode(new Integer(1));
	        js.setMessage("获取数据失败!");
	        User user = new User();
	        
	        if (user.getPageNo() == null)
	        	user.setPageNo(1);
	        user.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        try{
	            List<User> lc = userService.getUserListByOrgId(orgId);	
	            int totalCount = userService.getUserCountByOrgId(user);
	            user.setTotalCount(totalCount);
	            
	            User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，查询了所属机构下拥有的用户", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
	            js.setObj(user);
	            js.setCode(0);
	            js.setList(lc);
	            js.setMessage("获取数据成功!");
	        }
	        catch(Exception ex){
	            ex.printStackTrace();
	        }
	        return js;
		}
    	
	    	
	    	
	    /**
	     * 跳转到新增用户/编辑页面
	     * @return
	     */
	    @RequestMapping(value = "/userInfo.do", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	    @RequiresPermissions("system/user/userInfo.do")
	    public String editUser(@RequestParam(value = "id", required = false) Integer id,
				HttpServletRequest req, HttpServletResponse res){
	    	//根据参数id判断是新增还是编辑，新增为0，不用传值；编辑为选中的参数id值，取对象，传值
			if(id != null && id != 0){
				User user = new User();
				try{
					user = userService.getUserById(id);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				req.setAttribute("User", user);
			}
			
			//获取角色的集合
			Role role=new Role();
			List<Role> roleList = roleService.getRoleList(role);
			req.setAttribute("roleList",roleList);
			
			//根据用户ID去查询用户的角色
			List<Role> userRoleList=roleService.getRolesByUserId(id);
			req.setAttribute("userRoleList", userRoleList);
			
			//根据用户ID查询用户所属的机构,用于编辑的时候回显用户所属机构
			List<Organ> userOrgList= organService.getOrgsByUserId(id);
			req.setAttribute("userOrgList", userOrgList);
			
			/*//获取机构列表
			Organ organ =new Organ();
			List<Organ> organList = organService.getOrganList(organ);
			req.setAttribute("organList",organList);*/
			
			//获取职务列表			
			List<Meta> meatListByKey = configService.getMeatListByKey(Constants.META_POSITION_KEY);
			req.setAttribute("meatListByKey", meatListByKey);
			
			//根据用户ID查询用户所对应的职务,用于编辑的时候回显用户所属职务
			List<UserOrgan> userPostList= organService.getPostsByUserId(id);
			req.setAttribute("userPostList", userPostList);
			
			
	    	return "web/base/user/userInfo";
	    }
		
	    /** 
		 * 新增,编辑用户
		 */
	    @ResponseBody
	    @RequestMapping(value = "/jsonSaveOrUpdateUser.do", method=RequestMethod.POST)
	    @RequiresPermissions("system/user/jsonSaveOrUpdateUser.do")
	    public JsonResult<User> jsonSaveOrUpdateDevice(User user,
	    		@RequestParam(value = "roleId", required = false) Integer[] roleIds, //用户所属的角色的ID的集合
	    		@RequestParam(value = "orgId", required = false) Integer[] orgIds, //用户所属机构的ID的集合
	    		@RequestParam(value = "userOrgId", required = false) Integer[] userOrgIds, //用户-机构中间表的ID的集合
	    		@RequestParam(value = "postId", required = false) Integer postId, //用户对应的职务的集合	    		
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
				
				//创建用于新增时根据用户账号去查询用户是否存在的user对象
				User u = new User();
				u.setId(user.getId());
				u.setName(user.getName());
				u.setAccount(user.getAccount());
				
				//如为编辑，则给新建user对象赋传来的设备id值
				if (user.getId() > 0) {
					u.setId(user.getId());					
					State = true;//userService.saveOrUpdateUser(user,roleIds,orgIds,postId);
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
					State = true;// userService.saveOrUpdateUser(user,roleIds,orgIds,postId);
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
					js.setMessage("该用户已存在!");
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
		@RequiresPermissions("system/user/jsondeleteUserById.do")
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
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，删除了"+user.getName()+"用户", 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("删除成功!");
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
	     * 修改用户状态
	     */
	    @ResponseBody
		@RequestMapping(value = "/jsonupdateUserById.do", method = RequestMethod.POST)
		@RequiresPermissions("system/user/jsonupdateUserById.do")
		public JsonResult<User> jsonupdateUserById(User user,				
				HttpServletRequest request, HttpServletResponse response) {
			
			// 新建一个json对象 并赋初值
			JsonResult<User> js = new JsonResult<User>();
			js.setCode(new Integer(1));
			js.setMessage("修改用户状态失败!");			
			
			try {
				if(user.getUsed()==1){
					user.setUsed(0);
				}else if(user.getUsed()==0){
					user.setUsed(1);
				}
				boolean state = userService.updateUserUsedById(user);
				User user2 = userService.getUserById(user.getId());
				if(state){
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，修改了用户："+user2.getName()+"的状态", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("修改用户状态成功!");
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
		@RequiresPermissions("system/user/jsonResetUserPwd.do")
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
	     * 加载机构的树
	     * @param request
	     * @param response
	     * @return
	     */
	    /*@ResponseBody
		@RequestMapping(value = "/jsonLoadOrganTreeList.do", method = RequestMethod.POST)
		@RequiresPermissions("system/user/jsonLoadOrganTreeList.do")
		public JsonResult<Organ> jsonLoadOrganTreeList(				
				HttpServletRequest request, HttpServletResponse response) {
	    	JsonResult<Organ> js = new JsonResult<Organ>();
	    	//获取机构列表
			Organ organ =new Organ();
			List<Organ> organList = organService.getOrganList(organ);
			js.setList(organList);
			
	    	return js;
	    }*/
	    
	    

}
