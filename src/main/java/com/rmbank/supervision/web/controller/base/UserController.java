package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.common.DataListResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.Organ;
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
//    	User lgUser = this.getLoginUser();
//    	List<Organ> userOrgList=userService.getUserOrgByUserId(lgUser.getId());
//    	List<Organ> userOrgList=userService.getUserOrgByUserId(2);
    	//判断当前登录账号是不是超级管理员
//		if(lgUser.getAccount().equals(Constants.USER_SUPER_ADMIN_ACCOUNT) 
//				|| userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_1 
//				||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_2
//				||userOrgList.get(0).getOrgtype()==Constants.ORG_TYPE_3){
			try{
				//t_user取满足要求的参数数据
				userList =  userService.getUserList(user);					
				//t_user取满足要求的记录总数
				totalCount = userService.getUserCount(user);
			}catch(Exception ex){ 
				ex.printStackTrace();
			}	
//		}else {
//			try{
//				lgUser.setSearchName(user.getSearchName()); 
//				
//				//获取当前登录用户所属的机构ID
//				List<Integer> userOrgIds=userService.getUserOrgIdsByUserId(lgUser.getId());
//				//将用户所属的机构id存入到session中
//				HttpSession session = request.getSession();
//				session.setAttribute("userOrgIds", userOrgIds); 
//				
//				//根据机构ID查询用户
//				userList=userService.getUserByOrgids(userOrgIds);					
//				totalCount = userService.getUserCountByOrgId(lgUser);
// 				
//
//			}catch(Exception ex){ 
//				ex.printStackTrace();
//			}	 
//		}
		user.setTotalCount(totalCount); 	

		dr.setData(user);
		dr.setDatalist(userList); 
    	return dr;
    }
}
