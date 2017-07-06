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

import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.Meta;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.OrganService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;

@Scope("prototype")
@Controller
@RequestMapping("/system/organ/")
public class OrganController extends SystemAction {

	@Resource
	private UserService userService;
	@Resource
	private OrganService organService;
	@Resource
	private ConfigService configService;
	@Resource
	private SysLogService logService;

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
		List<Organ> list =new ArrayList<Organ>();
		if (pid != null && pid>0) {
			organ.setPid(pid);
		} else {
			//获取当前登录用户
			User loginUser = this.getLoginUser();
			//获取当前用户对应的机构列表
			List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
			//获取当前用户对应的第一个机构
			Organ userOrg=userOrgList.get(0);
			if(userOrg.getOrgtype() == Constants.ORG_TYPE_4){
				organ.setPid(0);
				//获取用户所属的机构  
				list = organService.getOrganByPId(organ);	 
			}else{
				organ.setPid(userOrg.getId());
				Organ organ2 = organService.selectByPrimaryKey(userOrg.getId());
				list.add(organ2);
			}
		} 
		
		return list;// json.toString();
	}  

	/**
	 * 加载所有的机构
	 *  
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonLoadAllOrganList.do")
	public List<Organ> jsonLoadOrganList( 
			HttpServletRequest request, HttpServletResponse response) { 
		Organ organ = new Organ();
		organ.setPageNo(1);
		organ.setPageSize(9);
		List<Organ> list = organService.getOrganList(organ);	 
		return list;// json.toString();
	}  
	/**
	 * 根据机构Id，加载机构信息
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonLoadOrganInfo.do")
	public Organ jsonLoadOrganList(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		Organ organ = organService.selectByPrimaryKey(id);	 
		if(organ.getPid()>0){
			Organ pOrg = organService.selectByPrimaryKey(organ.getPid());
			if(pOrg != null){
				organ.setParentName(pOrg.getName());
			}else{
				organ.setParentName("根节点");
			}
		}else{
			organ.setParentName("根节点");
		}
		return organ;// json.toString();
	}  
	

	/**
	 * 根据机构Id，加载机构信息
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonDeleteOrgan.do")
	public JsonResult<Organ> jsonDeleteOrgan(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		JsonResult<Organ> js = new JsonResult<Organ>();
		js.setCode(new Integer(1));
		js.setMessage("机构删除失败!");
		Organ organ = organService.selectByPrimaryKey(id);	 
		if(organ != null){
			organService.deleteByPrimaryKey(id);
			js.setCode(0);
			js.setMessage("机构删除成功");
		} 
		return js;// json.toString();
	}  
	/**
	 * 新增/编辑机构
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateOrgan.do", method = RequestMethod.POST)
	public JsonResult<Organ> jsonSaveOrUpdateOrgan(Organ organ,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<Organ> js = new JsonResult<Organ>();
		js.setCode(new Integer(1));
		js.setMessage("机构信息保存失败!");

		boolean state = false;
		try {
			  
			// 如为编辑，则给新建role对象赋传来的id值
			if (organ.getId() > 0) { 
				state = organService.saveOrUpdateOrgan(organ);
				if (state) {
					
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，执行修改机构的信息", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}
			// 根据机构名称去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配机构名称是否重复
			else{
				state = organService.saveOrUpdateOrgan(organ);
				if (state) {				
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，新增了“"+organ.getName()+"”机构", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}  
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("机构信息保存出现异常!");
		}
		return js;
	}
	
}
