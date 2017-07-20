package com.rmbank.supervision.web.controller.cases;

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

import com.rmbank.supervision.common.DataListResult;
import com.rmbank.supervision.common.JsonResult;
import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.FunctionMenu;
import com.rmbank.supervision.model.GradeScheme;
import com.rmbank.supervision.model.GradeSchemeDetail;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.ResourceConfig;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.GradeSchemeDetailService;
import com.rmbank.supervision.service.GradeSchemeService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.web.controller.SystemAction;


/**
 * 方案明细的Action
 * @author DELL
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/manage/casedetail")
public class CasedetailController extends SystemAction {
	@Resource
	private GradeSchemeDetailService  gradeSchemeDetailService;
	@Resource
	private GradeSchemeService gradeSchemeService;
	@Resource
	private SysLogService logService;

	

	/***********************************************/
	/******************量化模型指标管理*********************/
	/***********************************************/
	/**
	 * 获取量化模型列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
    @RequestMapping(value = "/caseModelList.do")
    public List<GradeScheme> GradeSchemeList( 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
		List<GradeScheme> gradeSchemeList = new ArrayList<GradeScheme>();
		try {
			// t_GradeScheme取满足要求的参数数据
			gradeSchemeList = gradeSchemeService.getGradeSchemeList(new GradeScheme()); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	return gradeSchemeList;
    }
	
	/**
	 * 获取量化模型列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
    @RequestMapping(value = "/loadDetailByGradeIdAndLevel.do")
    public List<GradeSchemeDetail> loadDetailByGradeIdAndLevel( 
    		GradeSchemeDetail gradeSchemeDetail,
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
		List<GradeSchemeDetail> detailList = new ArrayList<GradeSchemeDetail>();
		try {
			// t_GradeScheme取满足要求的参数数据
			detailList = gradeSchemeDetailService.getGradeSchemeDetailListByGradeId(gradeSchemeDetail); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	return detailList;
    }
	/**
	 * 获取量化模型列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
    @RequestMapping(value = "/loadDetailByPid.do")
    public List<GradeSchemeDetail> loadDetailByPid( 
			@RequestParam(value = "pid", required = false) Integer id,
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
		List<GradeSchemeDetail> detailList = new ArrayList<GradeSchemeDetail>();
		try {
			GradeSchemeDetail gradeSchemeDetail = new GradeSchemeDetail();
			gradeSchemeDetail.setPid(id);
			// t_GradeScheme取满足要求的参数数据
			detailList = gradeSchemeDetailService.getGradeSchemeDetailListByPid(gradeSchemeDetail); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	return detailList;
    }
	
	
	/**
	 * 获取量化模型列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
    @RequestMapping(value = "/saveOrUpdateModelDetail.do")
    public JsonResult<GradeSchemeDetail> saveOrUpdateModelDetail( 
    		GradeSchemeDetail gradeSchemeDetail,
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 

		JsonResult<GradeSchemeDetail> js = new JsonResult<GradeSchemeDetail>();
		js.setCode(new Integer(1));
		js.setMessage("量化指标保存失败!");
		try {
			if(gradeSchemeDetail != null){
				//编辑的时候，如果分值有改动，则将模型置为禁用的，未改动，则不用修改之前的状态
				if(gradeSchemeDetail.getId()>0){
					GradeSchemeDetail temp = gradeSchemeDetailService.selectByPrimaryKey(gradeSchemeDetail.getId());
					if(temp != null){
						if(temp.getGrade() != gradeSchemeDetail.getGrade()){
							GradeScheme gs = gradeSchemeService.selectByPrimaryKey(temp.getGradeId());
							if(gs != null){
								gs.setUsed(0);
								gradeSchemeService.updateByPrimaryKeySelective(gs);
							}
						}
					}
				}else{
					//新增的时候，将模型置为禁用的
					GradeScheme gs = gradeSchemeService.selectByPrimaryKey(gradeSchemeDetail.getGradeId());
					if(gs != null){
						gs.setUsed(0);
						gradeSchemeService.updateByPrimaryKeySelective(gs);
					}
				}
				gradeSchemeDetailService.saveOrUpdateDetail(gradeSchemeDetail);
				User loginUser = this.getLoginUser();
				String ip = IpUtil.getIpAddress(request);		
				logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，保存了量化模型指标："+gradeSchemeDetail.getName() , 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
				js.setCode(0);
				js.setMessage("量化指标保存成功");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			js.setMessage("量化指标保存出现异常!");
		}
    	return js;
    }

	/**
	 * 根据机构Id，删除模型
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonDeleteModelDetail.do")
	public JsonResult<GradeSchemeDetail> jsonDeleteModelDetail(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		JsonResult<GradeSchemeDetail> js = new JsonResult<GradeSchemeDetail>();
		js.setCode(new Integer(1));
		js.setMessage("量化指标删除失败!");
		GradeSchemeDetail gradeSchemeDetail = gradeSchemeDetailService.selectByPrimaryKey(id);	 
		if(gradeSchemeDetail != null){
			gradeSchemeDetailService.deleteByPrimaryKey(id);
			js.setCode(0);
			js.setMessage("量化指标删除成功");
		} 
		return js;// json.toString();
	}  
	/***********************************************/
	/******************量化模型指标管理结束*********************/
	/***********************************************/
}
