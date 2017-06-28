package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.rmbank.supervision.model.Role;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ConfigService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.web.controller.SystemAction;


@Scope("prototype")
@Controller
@RequestMapping("/system/config")
public class ConfigController extends SystemAction {
	/**
	 *资源注入
	 */
	@Resource 
	private ConfigService configService;
	
	@Resource
	private SysLogService logService;
	 

	/**
	 * 加载所有的根配置
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadConfigList.do")
	public List<Meta> loadConfigList(
			Meta meta, 
			HttpServletRequest request, HttpServletResponse response) {
		List<Meta> configList = new ArrayList<Meta>();
		try {
			if(meta.getPid()==null){
				meta.setPid(0);
			}
			// t_meta取满足要求的参数数据
			configList = configService.getConfigListByPid(meta);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 通过request对象传值到前台
		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了配置列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return configList;
	}  

	/**
	 * 加载所有的根配置
	 * 
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loadConfigListBypId.do")
	public List<Meta> loadConfigListBypId( 
			@RequestParam(value = "pid", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		List<Meta> configList = new ArrayList<Meta>();
		try {
			Meta meta = new Meta();
			if(id != -1){
				meta.setPid(id);
				// t_meta取满足要求的参数数据
				configList = configService.getConfigListByPid(meta);
			}else{
				configList = configService.getConfigList(new Meta());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 通过request对象传值到前台
		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了配置列表查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		return configList;
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
	@RequestMapping(value = "/jsonLoadConfigInfo.do")
	public Meta jsonLoadConfigInfo(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		Meta meta = configService.selectByPrimaryKey(id);	 
		return meta;// json.toString();
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
	@RequestMapping(value = "/jsonDeleteConfig.do")
	public JsonResult<Meta> jsonDeleteConfig(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		JsonResult<Meta> js = new JsonResult<Meta>();
		js.setCode(new Integer(1));
		js.setMessage("机构删除失败!");
		Meta meta = configService.selectByPrimaryKey(id);	 
		if(meta != null){
			meta.setUsed(0);
			configService.saveOrUpdateMeta(meta);
			js.setCode(0);
			js.setMessage("配置删除成功");
		} 
		return js;// json.toString();
	}  
	/**
	 * 新增/编辑机构
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateConfig.do", method = RequestMethod.POST)
	public JsonResult<Meta> jsonSaveOrUpdateOrgan(Meta meta,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<Meta> js = new JsonResult<Meta>();
		js.setCode(new Integer(1));
		js.setMessage("配置信息保存失败!");
		try {
			  configService.saveOrUpdateMeta(meta);
				js.setCode(0);
				js.setMessage("配置信息保存成功!");
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("配置信息保存出现异常!");
		}
		return js;
	}
}
