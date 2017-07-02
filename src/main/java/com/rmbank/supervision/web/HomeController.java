package com.rmbank.supervision.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.ReturnResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.EndecryptUtils;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.FunctionMenu;
import com.rmbank.supervision.model.Organ; 
import com.rmbank.supervision.model.Role; 
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.FunctionService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.RolePermissionService;
import com.rmbank.supervision.service.RoleResourceService;
import com.rmbank.supervision.service.RoleService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;
 







import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class HomeController extends SystemAction {

    @Resource(name="userService")
    private UserService userService;

    @Resource(name="functionService")
    private FunctionService functionService;

    @Resource(name="organService")
    private OrganService organService;

    @Resource(name="resourceService")
    private ResourceService resourceService;

    @Resource(name="roleService")
    private RoleService roleService;

    @Resource(name="roleResourceService")
    private RoleResourceService roleResourceService;
    
    @Resource(name="rolePermissionService")
    private RolePermissionService rolePermissionService;
    

	@Resource
	private SysLogService logService;
	

	@RequestMapping("homePage")  
    public ModelAndView homePage( 
			HttpServletRequest request, HttpServletResponse response){  
        //创建模型跟视图，用于渲染页面。并且指定要返回的页面为login页面
        ModelAndView mav = new ModelAndView("masterpage"); 
        return mav;
	}

	@RequestMapping("loginPage")  
    public ModelAndView loginPage( 
			HttpServletRequest request, HttpServletResponse response){  
        //创建模型跟视图，用于渲染页面。并且指定要返回的页面为login页面
        ModelAndView mav = new ModelAndView("login"); 
        return mav;
	}
	

	@RequestMapping("modelSelect")  
    public ModelAndView modelSelect( 
			HttpServletRequest request, HttpServletResponse response){  
        //创建模型跟视图，用于渲染页面。并且指定要返回的页面为login页面
        ModelAndView mav = new ModelAndView("modelSelect"); 
        return mav;
	}

    //创建模型跟视图，用于渲染页面。并且指定要返回的页面为login页面
//	FunctionMenu lf = new FunctionMenu();
    /***
     * 单个模块查看主页 
     * @return
     */
    @RequestMapping("index")
    public ModelAndView SSJCManiPage(
            HttpServletRequest request, HttpServletResponse response){
        ModelAndView mav = new ModelAndView("main");
        return mav;
    }    

	@ResponseBody
    @RequestMapping({"/createSingleSession.do"})
    public void createSingleSession(
            @RequestParam(value="key", required=false) String urlkey,
            HttpServletRequest request, HttpServletResponse response) {
		FunctionMenu lf = null;
    	if(!StringUtil.isEmpty(urlkey)){
	    	if(urlkey.equals("ssjc")){
	    		lf = getFunctionMenuByKey("vision/efficiency/efficiencyList.do",request);
	    	}else if(urlkey.equals("zhgl")){
	    		lf = getFunctionMenuByKey("manage/branch/branchFHList.do",request);
	    	}else if(urlkey.equals("jcgl")){
	    		lf = getFunctionMenuByKey("system/user/userList.do",request);
	    	}else if(urlkey.equals("rzgl")){
	    		lf = getFunctionMenuByKey("system/log/logList.do",request);
	    	}else if(urlkey.equals("tjfx")){
	    		lf = getFunctionMenuByKey("statistic/efficiency/efficencyStatistic.do",request);
	    	}
    	}
    	request.getSession().setAttribute(Constants.USER_SESSION_RESOURCE_SINGL_MODEL, lf);
    }
    
	private FunctionMenu getFunctionMenuByKey(String urlKey,HttpServletRequest request) {
		// TODO Auto-generated method stub
		FunctionMenu fm = null;
		User loginUser = (User)SecurityUtils.getSubject().getSession().getAttribute(Constants.USER_INFO);
		if(null != loginUser){
			List<FunctionMenu> list = (List<FunctionMenu>) request.getSession().getAttribute(Constants.USER_SESSION_RESOURCE);
			if(null != list && list.size()>0){
				for(FunctionMenu f : list){
					if(!StringUtil.isEmpty(f.getUrl())&& f.getUrl().equals(urlKey)){
						fm = f; 
						break;
					}
				}
			}
		}
		return fm;
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
		if (pid != null) {
			organ.setPid(pid);
		} else {
			organ.setPid(0);
		} 
		//获取用户所属的机构  
		List<Organ> list = organService.getOrganByPId(organ);	 
		return list;// json.toString();
	} 

	/**
     * 根据机构ID查询用户
     */
    @ResponseBody
	@RequestMapping(value = "/loadUserListByOrgId.do", method = RequestMethod.POST) 
	public List<User> loadUserListByOrgId(
			@RequestParam(value = "orgId", required = false) Integer orgId,
			HttpServletRequest request, HttpServletResponse response) { 
    	List<User> list = new ArrayList<User>();
        try{
        	list = userService.getUserListByOrgId(orgId);	 
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
	}
	
	
	
    @ResponseBody
    @RequestMapping(value={"/userLogin.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public JsonResult<User> userLogin(User user, HttpServletRequest request, HttpServletResponse response) {
        JsonResult<User> json = new JsonResult<User>();
        json.setCode(Constants.RESULT_FAILED);
        json.setMessage("登录失败!");
        try {
            ReturnResult<User> res = this.userService.login(user.getAccount(), user.getPwd(),false);
            if (res.getCode().intValue() == ReturnResult.SUCCESS) {
                User u = res.getResultObject(); 
                //获取该用户类型所有模块 
                List<FunctionMenu> lf = new ArrayList<FunctionMenu>(); 
                //不是超级管理员
                if(!u.getAccount().equals(Constants.USER_SUPER_ADMIN_ACCOUNT)){
                    //获取当前用户角色  
                    List<Role> roleList = roleService.getRolesByUserId(u.getId());
                    List<FunctionMenu> subList = functionService.getFunctionByUserId(u.getId()); 
                    if(subList.size() ==0){
                         json.setMessage("登录失败，当前登录用户未授权!");
                    }else{
                    	lf = getFunctionMenuListByItem(subList);
                    }
                }
                else{                	
                    lf = parseFunctionMenuList(this.functionService.getFunctionMenuByParentId(0));
                } 
                request.getSession().setAttribute(Constants.USER_SESSION_RESOURCE, lf);
               
                
                json.setGotoUrl(((FunctionMenu)lf.get(0)).getUrl()); 
                ((User)res.getResultObject()).setSelectedMainMemu(((FunctionMenu)lf.get(0)).getId().intValue());
                if(((FunctionMenu)lf.get(0)).getChildMenulist() != null){
                    ((User)res.getResultObject()).setSelectedChildMenu(((FunctionMenu)((FunctionMenu)lf.get(0)).getChildMenulist().get(0)).getId().intValue());
                    ((User)res.getResultObject()).setChildMenuList(((FunctionMenu)lf.get(0)).getChildMenulist());
                }else{
                    ((User)res.getResultObject()).setSelectedChildMenu(((FunctionMenu)lf.get(0)).getId().intValue());
                }
                
                //获取当前登录用户的机构
                List<Organ> userOrgByUserId = userService.getUserOrgByUserId(u.getId());
                Organ organ = userOrgByUserId.get(0);
                u.setUserOrgID(organ.getId());
                if(organ.getSupervision()==1){
                	u.setIsSupervision(true);
                }else{
                	u.setIsSupervision(false);
                }
                if(organ.getOrgtype()==Constants.ORG_TYPE_1 || organ.getOrgtype()==Constants.ORG_TYPE_2){
                	u.setIsBranch(true);                	
                }else {
                	u.setIsBranch(false);
				}
                setLoginUser(u);  /////
              
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+u.getName()+"登录了系统", 4, u.getId(), u.getUserOrgID(), ip);
				
                json.setCode(Constants.RESULT_SUCCESS);
                json.setMessage("登录成功!");
            } else {
                json.setMessage(res.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
    

    private List<FunctionMenu> getFunctionMenuListByItem(
			List<FunctionMenu> subList) {
		// TODO Auto-generated method stub
    	List<FunctionMenu> lf = new ArrayList<FunctionMenu>();
    	List<Integer> idList = new ArrayList<Integer>();
    	List<Integer> itemIdList = new ArrayList<Integer>();
		for(FunctionMenu fm: subList){
			if(idList.size() == 0){
				idList.add(fm.getParentId());
			}else if(!idList.contains(fm.getParentId())){
				idList.add(fm.getParentId());
			}
			itemIdList.add(fm.getId());
		}
		System.out.println(idList.size());
		for(int id : idList){
			FunctionMenu fms = functionService.getFunctionMenusById(id);
			lf.add(fms);
		}
		lf = parseFunctionMenuListByItemId(lf,itemIdList);  
		return lf;
	}
	private List<FunctionMenu> parseFunctionMenuListByItemId(List<FunctionMenu> src,List<Integer> itemIdList)
    {
        for (FunctionMenu f : src) {
            if(f.getLeaf() == 0) {
            	List<FunctionMenu> tempList = this.functionService.getFunctionMenuByParentId(f.getId()); 
            	List<FunctionMenu> cList = new ArrayList<FunctionMenu>();
            	for(FunctionMenu tf : tempList){
            		for(int id : itemIdList){
            			if(id == tf.getId()){
            				cList.add(tf);
            			}
            		}
            	}
                f.setChildMenulist(cList);
            }
        }
        return src;
    }
	private List<FunctionMenu> parseFunctionMenuList(List<FunctionMenu> src)
    {
        for (FunctionMenu f : src) {
            if(f.getLeaf() == 0) {
                f.setChildMenulist(this.functionService.getFunctionMenuByParentId(f.getId()));
            }
        }
        return src;
    }
	 
	@ResponseBody
    @RequestMapping({"/loadUserResourceSession.do"})
    public List<FunctionMenu> loadUserResourceSession(
            HttpServletRequest request, HttpServletResponse response) {
		List<FunctionMenu> list = new ArrayList<FunctionMenu>();
		User loginUser = (User)SecurityUtils.getSubject().getSession().getAttribute(Constants.USER_INFO);
		if(null != loginUser){
			list = (List<FunctionMenu>) request.getSession().getAttribute(Constants.USER_SESSION_RESOURCE);
		}
        return list;
    }

	 
	@ResponseBody
    @RequestMapping({"/loadUserSingleResourceSession.do"})
    public FunctionMenu loadUserSingleResourceSession(
            HttpServletRequest request, HttpServletResponse response) {
		FunctionMenu fm = new FunctionMenu();
		User loginUser = (User)SecurityUtils.getSubject().getSession().getAttribute(Constants.USER_INFO);
		if(null != loginUser){
			fm = (FunctionMenu) request.getSession().getAttribute(Constants.USER_SESSION_RESOURCE_SINGL_MODEL);
		}
        return fm;
    }
	 
	@ResponseBody
    @RequestMapping({"/logout.do"})
    public ModelAndView logout(
            HttpServletRequest request, HttpServletResponse response) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存 
		}
		ModelAndView mav = new ModelAndView("masterpage"); 
        return mav;
    }
    @ResponseBody
    @RequestMapping({"/jsonLoadSession.do"})
    public JsonResult<User> jsonLoadSession(
            @RequestParam(value="selectedMainMemu", required=false) Integer selectedMainMemu,
            @RequestParam(value="selectedChildMenu", required=false) Integer selectedChildMenu,
            HttpServletRequest request, HttpServletResponse response) {
        JsonResult<User> json = new JsonResult<User>();
        if (selectedMainMemu != null) {
            getLoginUser().setSelectedMainMemu(selectedMainMemu.intValue());
            List<FunctionMenu> lf = (List<FunctionMenu>) request.getSession().getAttribute(Constants.USER_SESSION_RESOURCE);
            for (FunctionMenu resource : lf) {
                if (resource.getId().intValue() == selectedMainMemu.intValue()) {
                    if(resource.getChildMenulist() != null) {
                        getLoginUser().setSelectedChildMenu(resource.getChildMenulist().get(0).getId());
                    }else{
                        getLoginUser().setSelectedChildMenu(resource.getId().intValue());
                    }
                    break;
                }
            }
        }
        else if (selectedChildMenu != null) {
            getLoginUser().setSelectedChildMenu(selectedChildMenu.intValue());
        }
        json.setCode(new Integer(0));
        json.setMessage("更新成功!");

        return json;
    }

	 
	@ResponseBody
    @RequestMapping({"/loadLoginUserInfo.do"})
    public User loadLoginUserInfo(
            HttpServletRequest request, HttpServletResponse response) {
		User loginUser = (User)SecurityUtils.getSubject().getSession().getAttribute(Constants.USER_INFO);
		List<Organ> userOrgByUserId = userService.getUserOrgByUserId(loginUser.getId());
		Organ organ = userOrgByUserId.get(0);
		if(organ.getPid()>0){
			Organ pOrgan = organService.selectByPrimaryKey(organ.getPid());
			loginUser.setOrgName(pOrgan.getName() +"->"+organ.getName());
		}else{
			loginUser.setOrgName(organ.getName());
		}
        return loginUser;
    }
	@ResponseBody
    @RequestMapping({"/changeUserPwd.do"})
    public JsonResult<User> changeUserPwd( 
    		User user,
            HttpServletRequest request, HttpServletResponse response) {
        JsonResult<User> js = new JsonResult<User>();
        js.setCode(1);
        js.setMessage("修改当前登录用户密码失败");
        try{
        	if(StringUtil.isEmpty(user.getOldPassword())) {
                js.setMessage("修改当前登录用户密码失败,原密码不能为空");
                return js;
            }
            if(StringUtil.isEmpty(user.getNewPassword())) {
                js.setMessage("修改当前登录用户密码失败,新密码不能为空");
                return js;
            }
            if(user.getOldPassword().equals(user.getNewPassword())){
                js.setMessage("修改当前登录用户密码失败,新密码不能与原密码相同");
                return js;
            }
            if(StringUtil.isEmpty(user.getRePassword())) {
                js.setMessage("修改当前登录用户密码失败,确认密码不能为空");
                return js;
            }
            if(!user.getRePassword().equals(user.getNewPassword())) {
                js.setMessage("修改当前登录用户密码失败,两次密码不一致，请确认重新输入");
                return js;
            }
            /*检验原密码是否正确*/
            User user2 = userService.getUserById(user.getId());
            ReturnResult<User> res = userService.login(user2.getAccount(), user.getOldPassword(), false);
            if (res.getCode().intValue() == ReturnResult.SUCCESS) {
                /*新密码加密*/
                User u = EndecryptUtils.md5Password(user2.getAccount(), user.getNewPassword());
                if (u != null) {
                    user.setPwd(u.getPwd());
                    user.setSalt(u.getSalt());
                    user.setIsChangePwd(1);
                    userService.updateByPrimaryKeySelective(user);
                    User loginUser = (User)request.getSession().getAttribute(Constants.USER_INFO);
                    loginUser.setIsChangePwd(1);
                    request.getSession().setAttribute(Constants.USER_INFO,loginUser);
                    js.setCode(0);
                    js.setMessage("修改密码成功！");
                }else{
                	js.setMessage("修改当前登录用户密码失败,使用新密码创建密钥的时候出错，请联系管理员");
                }
            }else{
                js.setMessage("修改当前登录用户密码失败,原密码错误");
            } 
        }catch(Exception ex){
        	ex.printStackTrace();
        	js.setMessage("修改当前登录用户密码发生异常");
        }
        return js;
    }
	
    
}
