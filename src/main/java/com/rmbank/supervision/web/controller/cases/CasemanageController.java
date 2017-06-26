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
	
	
	/**
	 * 量化模型管理列表
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
    @RequestMapping(value = "/casemanageList.do")
//	@RequiresPermissions("manage/casemanage/gradeSchemeList.do")
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
	
}
