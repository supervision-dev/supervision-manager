package com.rmbank.supervision.web.controller.cases;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
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
import com.rmbank.supervision.model.GradeScheme;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.GradeSchemeDetailService;
import com.rmbank.supervision.service.GradeSchemeService;
import com.rmbank.supervision.service.SysLogService;
import com.rmbank.supervision.web.controller.SystemAction;



/**
 * 量化模型管理的Controller
 * @author DELL
 *
 */
@Scope("prototype")
@Controller
@RequestMapping("/manage/casemanage")
public class CasemanageController extends SystemAction {

	@Resource
	private GradeSchemeService gradeSchemeService;
	@Resource
	private GradeSchemeDetailService gradeSchemeDetailService;
	@Resource
	private SysLogService logService;
	
	/***********************************************/
	/******************量化模型管理*********************/
	/***********************************************/
	/**
	 * 量化模型管理列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
    @RequestMapping(value = "/casemanageList.do")
    public DataListResult<GradeScheme> GradeSchemeList(GradeScheme gradeScheme, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	DataListResult<GradeScheme> dr = new DataListResult<GradeScheme>();
    	//判断搜索名是否为空，不为空则转为utf-8编码 		
		if(gradeScheme.getSearchName() != null && gradeScheme.getSearchName() != ""){
			String searchName =  new String(gradeScheme.getSearchName().getBytes(
					"iso8859-1"), "utf-8");
			gradeScheme.setSearchName(searchName);
		}
		//设置页面初始值及页面大小
		if (gradeScheme.getPageNo() == null)
			gradeScheme.setPageNo(1);
		gradeScheme.setPageSize(Constants.DEFAULT_PAGE_SIZE);  
		int totalCount =  0;		
		// 分页集合
		List<GradeScheme> gradeSchemeList = new ArrayList<GradeScheme>();
		try {
			// t_GradeScheme取满足要求的参数数据
			gradeSchemeList = gradeSchemeService.getGradeSchemeList(gradeScheme);
			// t_GradeScheme取满足要求的记录总数
			totalCount = gradeSchemeService.getGradeSchemeCount(gradeScheme);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		gradeScheme.setTotalCount(totalCount);
		dr.setData(gradeScheme);
		dr.setDatalist(gradeSchemeList); 
    	return dr;
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
	@RequestMapping(value = "/jsonDeleteModel.do")
	public JsonResult<GradeScheme> jsonDeleteModel(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		JsonResult<GradeScheme> js = new JsonResult<GradeScheme>();
		js.setCode(new Integer(1));
		js.setMessage("量化模型删除失败!");
		GradeScheme gradeScheme = gradeSchemeService.selectByPrimaryKey(id);	 
		if(gradeScheme != null){
			gradeSchemeService.deleteByPrimaryKey(id);
			js.setCode(0);
			js.setMessage("量化模型删除成功");
		} 
		return js;// json.toString();
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
		@RequestMapping(value = "/jsonLockModel.do")
		public JsonResult<GradeScheme> jsonLockModel(
				GradeScheme gradeScheme,
				HttpServletRequest request, HttpServletResponse response) {
			JsonResult<GradeScheme> js = new JsonResult<GradeScheme>();
			js.setCode(new Integer(1));
			js.setMessage("量化模型禁用失败!");
			if(gradeScheme != null && gradeScheme.getId()>0){
				gradeScheme = gradeSchemeService.selectByPrimaryKey(gradeScheme.getId());	 
				if(gradeScheme != null){
					gradeScheme.setUsed(0);
					gradeSchemeService.updateByPrimaryKeySelective(gradeScheme);
					js.setCode(0);
					js.setMessage("量化模型禁用成功");
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，禁用了量化模型："+gradeScheme.getName() , 2, loginUser.getId(), loginUser.getUserOrgID(), ip);

				} 
			}
			return js;// json.toString();
		}
		/**
	     * 启用量化模型状态
	     */
	    @ResponseBody
		@RequestMapping(value = "/jsonEnableGradeScheme.do", method = RequestMethod.POST)
		public JsonResult<GradeScheme> jsonEnableGradeScheme(GradeScheme gradeScheme,			
				HttpServletRequest request, HttpServletResponse response) {
			
			// 新建一个json对象 并赋初值
			JsonResult<GradeScheme> js = new JsonResult<GradeScheme>();
			js.setCode(new Integer(1));
			js.setMessage("启用量化模型失败!"); 
			try {
				
				gradeScheme = gradeSchemeService.selectByPrimaryKey(gradeScheme.getId());
				if(gradeScheme != null){
					GradeSchemeDetail gradeSchemeDetail = new GradeSchemeDetail();
					gradeSchemeDetail.setGradeId(gradeScheme.getId());
					List<GradeSchemeDetail> detailList = gradeSchemeDetailService.getGradeSchemeDetailListByGradeId(gradeSchemeDetail);
					if(detailList != null && detailList.size()>0){
						int defaultvalue = 100;
						double fristModelValue = Double.MIN_VALUE;
						double secondModelValue = 0;
						double thdModelValue = 0; 
						for(GradeSchemeDetail gsd : detailList){
							if(gsd.getLevel() == 0){
								fristModelValue = fristModelValue + gsd.getGrade();
							}else if(gsd.getLevel() == 1){
								secondModelValue = secondModelValue + gsd.getGrade(); 
							}else if(gsd.getLevel() == 2){
								thdModelValue = thdModelValue + gsd.getGrade();
							}
						}
						if(((int)fristModelValue % defaultvalue) != 0){
							js.setMessage("启用量化模型失败，该模型量化一级指标权重值：不等于100!");
							return js;
						}  
						
						if(((int)secondModelValue % defaultvalue) != 0){
							js.setMessage("启用量化模型失败，该模型量化各二级指标权重值：不等于100!");
							return js;
						}
						
						if(((int)thdModelValue % defaultvalue) != 0){
							js.setMessage("启用量化模型失败，该模型量化三级指标权标准分：不等于100分!");
							return js;
						} 
						gradeScheme.setUsed(1);
						gradeSchemeService.updateByPrimaryKeySelective(gradeScheme);
						js.setCode(0);
						js.setMessage("启用量化模型成功!");
						User loginUser = this.getLoginUser();
						String ip = IpUtil.getIpAddress(request);		
						logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，启用了量化模型："+gradeScheme.getName() , 2, loginUser.getId(), loginUser.getUserOrgID(), ip);

						return js;
					}else{
						js.setMessage("启用量化模型失败，该模型未量化任何指标!");
						return js;
					}
				}else{
					js.setMessage("启用量化模型失败，获取量化模型内容失败!");
					return js;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
			return js;
		} 
	/**
	 * 新增/编辑机构
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateModel.do", method = RequestMethod.POST)
	public JsonResult<GradeScheme> jsonSaveOrUpdateModel(GradeScheme gradeScheme,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<GradeScheme> js = new JsonResult<GradeScheme>();
		js.setCode(new Integer(1));
		js.setMessage("量化模型信息保存失败!");

		boolean state = false;
		try {
			  
			if (gradeScheme.getId() > 0) { 
				state = gradeSchemeService.saveOrUpdateScheme(gradeScheme);
				if (state) {
					
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，执行修改量化模型“"+gradeScheme.getName()+"”的信息", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}
			else{
				gradeScheme.setOrgId(null);
				gradeScheme.setInherit(1);
				gradeScheme.setUsed(0);
				state = gradeSchemeService.saveOrUpdateScheme(gradeScheme);
				if (state) {				
					User loginUser = this.getLoginUser();
					String ip = IpUtil.getIpAddress(request);		
					logService.writeLog(Constants.LOG_TYPE_BASE_DATA, "用户："+loginUser.getName()+"，新增了“"+gradeScheme.getName()+"”量化模型", 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
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
	

	/***********************************************/
	/******************量化模型管理结束*********************/
	/***********************************************/
	
	
}
