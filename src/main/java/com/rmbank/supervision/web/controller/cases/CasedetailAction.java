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
public class CasedetailAction extends SystemAction {
	@Resource
	private GradeSchemeDetailService  gradeSchemeDetailService;
	@Resource
	private GradeSchemeService gradeSchemeService;
	@Resource
	private SysLogService logService;
	
	/**
	 * 方案明细列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/casedetailList.do")
	@RequiresPermissions("manage/casedetail/casedetailList.do")
	public String casedetailList(GradeSchemeDetail gradeSchemeDetail,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		 
		// 分页集合
		List<GradeSchemeDetail> detailList = new ArrayList<GradeSchemeDetail>();
		List<GradeScheme> gradeSchemeList = new ArrayList<GradeScheme>();
		int defalult = 0;
		try {
			//取满足要求的参数数据
			gradeSchemeList =  gradeSchemeService.getGradeSchemeList(new GradeScheme()); 
			if(gradeSchemeList.size()>0){
				defalult = gradeSchemeList.get(0).getId();
			}
			gradeSchemeDetail.setGradeId(defalult);
			//取满足要求的参数数据			
			detailList = gradeSchemeDetailService.getGradeSchemeDetailListByGradeId(gradeSchemeDetail); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 通过request对象传值到前台 
		request.setAttribute("SchemeDetail", gradeSchemeDetail); 
		request.setAttribute("DetailList", detailList);
		request.setAttribute("SchemeId", defalult);
		
		User loginUser = this.getLoginUser();
		String ip = IpUtil.getIpAddress(request);		
		logService.writeLog(Constants.LOG_TYPE_SYS, "用户："+loginUser.getName()+"，执行了量化指标列表的查询", 4, loginUser.getId(), loginUser.getUserOrgID(), ip);
		
		return "web/manage/casedetail/casedetailList";
	}
	 
    /**
     * 删除方案明细
     * @param id
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/jsondeleteDetailById.do", method = RequestMethod.POST)
	@RequiresPermissions("manage/casedetail/jsondeleteDetailById.do")
	public JsonResult<GradeSchemeDetail> jsondeleteGradeSchemeById(
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 新建一个json对象 并赋初值
		JsonResult<GradeSchemeDetail> js = new JsonResult<GradeSchemeDetail>();
		js.setCode(new Integer(1));
		js.setMessage("删除失败!");
		try {	
			GradeSchemeDetail detail = gradeSchemeDetailService.selectByPrimaryKey(id);
			int state= gradeSchemeDetailService.deleteByPrimaryKey(id);
			
			if(state==1){
				User loginUser = this.getLoginUser();
	    		String ip = IpUtil.getIpAddress(request);		
	    		logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，删除了量化指标："+detail.getName(), 3, loginUser.getId(), loginUser.getUserOrgID(), ip);
	    		
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
	 * 跳转到新增/编辑方案
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/casedetailInfo.do")
	@RequiresPermissions("manage/casedetail/casedetailInfo.do")
	public String exitCasedetail(GradeSchemeDetail detail,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		
		
		GradeSchemeDetail schemeDetail= new GradeSchemeDetail();
		List<GradeScheme> gradeSchemeList = new ArrayList<GradeScheme>();
		//若为编辑回显指标属性
		if(detail.getId() != null && detail.getId() != 0){ 
			schemeDetail=gradeSchemeDetailService.getGradeSchemeDetailById(detail.getId()); 
			if(schemeDetail.getLevel() >0){
				GradeSchemeDetail temp = gradeSchemeDetailService.getGradeSchemeDetailById(schemeDetail.getPid()); 
				if(temp != null){
					schemeDetail.setGradeId(temp.getGradeId());
					schemeDetail.setpName(temp.getName());
					if(temp.getPid()>0){
						temp = gradeSchemeDetailService.getGradeSchemeDetailById(temp.getPid());
						if(temp != null){ 
							schemeDetail.setPpName(temp.getName());
						}
					}
				}
			}
		}else{
			schemeDetail.setId(0);
			schemeDetail.setLevel(detail.getLevel());
			schemeDetail.setPid(detail.getPid()); 
			if(detail.getPid()>0){
				GradeSchemeDetail temp = gradeSchemeDetailService.getGradeSchemeDetailById(detail.getPid()); 
				if(temp != null){
					schemeDetail.setGradeId(temp.getGradeId());
					schemeDetail.setpName(temp.getName());
					if(temp.getPid()>0){
						temp = gradeSchemeDetailService.getGradeSchemeDetailById(temp.getPid());
						if(temp != null){ 
							schemeDetail.setPpName(temp.getName());
						}
					}
				}
			}
		}

		//取满足要求的参数数据
		gradeSchemeList =  gradeSchemeService.getGradeSchemeList(new GradeScheme()); 
		request.setAttribute("SchemeDetail", schemeDetail);

		request.setAttribute("SchemeList", gradeSchemeList);
		return "web/manage/casedetail/casedetailInfo";
	} 
	/**
	 * 新增/编辑指标
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonSaveOrUpdateDetail.do", method = RequestMethod.POST)
	@RequiresPermissions("manage/casedetail/jsonSaveOrUpdateDetail.do")
	public JsonResult<GradeSchemeDetail> jsonSaveOrUpdateDetail(GradeSchemeDetail detail,
			HttpServletRequest request, HttpServletResponse response) {

		// 新建一个json对象 并赋初值
		JsonResult<GradeSchemeDetail> js = new JsonResult<GradeSchemeDetail>();
		js.setCode(new Integer(1)); 
		js.setMessage("保存失败!");
		boolean state = false;
		try { 
			GradeSchemeDetail gsd = new GradeSchemeDetail();
			gsd.setId(detail.getId());
			gsd.setName(detail.getName());
			// 如为编辑，则给新建ResourceConfig对象赋传来的id值,并根据ID去修改
			if (detail.getId() > 0) {
				GradeSchemeDetail temp = gradeSchemeDetailService.selectByPrimaryKey(detail.getId());
				gsd.setId(detail.getId());
				state = gradeSchemeDetailService.saveOrUpdateDetail(detail);
				if (state) {
					if(temp != null && temp.getGrade() != null && temp.getGrade() != detail.getGrade()){
						GradeScheme gradeScheme = new GradeScheme();
						gradeScheme = gradeSchemeService.selectByPrimaryKey(temp.getGradeId());
						if(gradeScheme != null && gradeScheme.getUsed() == 1){
							gradeScheme.setUsed(0);
							gradeSchemeService.updateByPrimaryKeySelective(gradeScheme);
							System.out.println("更改了量化指标的分值，总分值可能会发生变化， 将模型置为不可用，重新启用，会执行检查");
						}
					}
					User loginUser = this.getLoginUser();
		    		String ip = IpUtil.getIpAddress(request);		
		    		logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，执行了对量化指标信息的修改", 2, loginUser.getId(), loginUser.getUserOrgID(), ip);
		    		
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			}
			// 根据指标名称（name）去数据库匹配，如编辑，则可以直接保存；如新增，则需匹配名称是否重复
			List<GradeSchemeDetail> lc = gradeSchemeDetailService.getExistDetail(gsd);
			if (lc.size() == 0) {
				state = gradeSchemeDetailService.saveOrUpdateDetail(detail);
				if (state) {
					GradeScheme gradeScheme = new GradeScheme();
					gradeScheme = gradeSchemeService.selectByPrimaryKey(gsd.getGradeId());
					if(gradeScheme != null && gradeScheme.getUsed() == 1){
						gradeScheme.setUsed(0);
						gradeSchemeService.updateByPrimaryKeySelective(gradeScheme);
						System.out.println("新增量化指标的分值，总分值可能会发生变化， 将模型置为不可用，重新启用，会执行检查");
					}
					User loginUser = this.getLoginUser();
		    		String ip = IpUtil.getIpAddress(request);		
		    		logService.writeLog(Constants.LOG_TYPE_LXGL, "用户："+loginUser.getName()+"，新增了量化指标："+detail.getName(), 1, loginUser.getId(), loginUser.getUserOrgID(), ip);
		    		
					js.setCode(new Integer(0));
					js.setMessage("保存成功!");
					return js;
				} else {
					return js;
				}
			} else {
				js.setMessage("该资源已存在!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return js;
	}
	
	/**
	 * 加载方案管理的树
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonLoadGradeSchemeTreeList.do")
	@RequiresPermissions("manage/casedetail/jsonLoadGradeSchemeTreeList.do")
	public List<GradeScheme> getGradeSchemeList( 
			HttpServletRequest request, HttpServletResponse response) { 
		//第一级父节点的集合
		List<GradeScheme> list = gradeSchemeService.getGradeSchemeListASC(new GradeScheme());
		GradeScheme gs = null;
		if(list.size() > 0){
			for(GradeScheme a:list){
				a.setText(a.getName());	 
				List<GradeScheme> itemList = new ArrayList<GradeScheme>();
				gs = new  GradeScheme();
				gs.setId(1);
				gs.setText("一级指标");
				gs.setIsLeaf(1);
				gs.setLevel(0);
				gs.setGradeId(a.getId());
				itemList.add(gs);
				gs = new  GradeScheme();
				gs.setId(1);
				gs.setText("二级指标");
				gs.setLevel(1);
				gs.setGradeId(a.getId());
				gs.setIsLeaf(1);
				itemList.add(gs);
				gs = new  GradeScheme();
				gs.setId(1);
				gs.setText("三级指标");
				gs.setLevel(2);
				gs.setIsLeaf(1);
				itemList.add(gs);
				gs.setGradeId(a.getId());
				a.setChildren(itemList);
				a.setIsLeaf(0);
				a.setState("open");
			}
		}
		
		return list;// json.toString();
	}

	/**
	 * 加载方案管理的树
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonLoadGradeSchemeDetailList.do")
	@RequiresPermissions("manage/casedetail/jsonLoadGradeSchemeDetailList.do")
	public List<GradeSchemeDetail> jsonLoadGradeSchemeDetailList( 
			GradeSchemeDetail gradeSchemeDetail,
			HttpServletRequest request, HttpServletResponse response) { 
		//第一级父节点的集合
		List<GradeSchemeDetail> list = new ArrayList<GradeSchemeDetail>();
		if(gradeSchemeDetail.getGradeId() != null && gradeSchemeDetail.getGradeId() > 0 ){  
			//取满足要求的参数数据			
			list = gradeSchemeDetailService.getGradeSchemeDetailListByGradeId(gradeSchemeDetail); 
		}
		return list;// json.toString();
	}
	/**
	 * 新增指标时加载上级指标的树
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonLoadSchemeDetailTreeList.do")
	@RequiresPermissions("manage/casedetail/jsonLoadSchemeDetailTreeList.do")
	public List<GradeSchemeDetail> jsonLoadSchemeDetailTreeList(
			@RequestParam(value = "pid", required = false) Integer pid,
			HttpServletRequest request, HttpServletResponse response) {
    	
		GradeSchemeDetail schemeDetail = new GradeSchemeDetail();	
		if (pid != null){
			schemeDetail.setPid(pid);
		}else {
			schemeDetail.setPid(0);
		}
		//第一级父节点的集合
		List<GradeSchemeDetail> list = gradeSchemeDetailService.getGradeSchemeDetailTreeByPid(schemeDetail);
		
		for(GradeSchemeDetail a:list){
			a.setText(a.getName());
			GradeSchemeDetail fun = new GradeSchemeDetail();
			fun.setPid(a.getId());
			List<GradeSchemeDetail> list1 = new ArrayList<GradeSchemeDetail>();
			list1 = gradeSchemeDetailService.getGradeSchemeDetailTreeByPid(fun);
			if(list1.size() > 0){
				list1 = setChildren(list1);
			}
			a.setChildren(list1);
			a.setState("open");
		}
		
		return list;// json.toString();
	}
	
	private List<GradeSchemeDetail> setChildren(List<GradeSchemeDetail> ls) {
		for (GradeSchemeDetail c : ls) {
			c.setText(c.getName());
			GradeSchemeDetail c1 = new GradeSchemeDetail();
			c1.setPid(c.getId());
			List<GradeSchemeDetail> lst = gradeSchemeDetailService.getGradeSchemeDetailTreeByPid(c1);
			if (lst.size() > 0) {
				lst = setChildren(lst);
				c.setChildren(lst);
				c.setState("open");
			} else {
				c.setChildren(new ArrayList<GradeSchemeDetail>());
				c.setState("open");
			}
		}
		return ls;// json.toString();
	}
	
	
}
