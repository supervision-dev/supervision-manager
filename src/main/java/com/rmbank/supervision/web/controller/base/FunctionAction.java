package com.rmbank.supervision.web.controller.base;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rmbank.supervision.model.FunctionMenu;
import com.rmbank.supervision.service.FunctionService;
import com.rmbank.supervision.web.controller.SystemAction;

@Scope("prototype")
@Controller
@RequestMapping("/system/function")
public class FunctionAction extends SystemAction{
	
	@Resource
	private FunctionService functionService;
	
	/**
	 * 加载所属模块的树
	 * @param pid
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jsonLoadFunctionTreeList.do")
	@RequiresPermissions("system/function/jsonLoadFunctionTreeList.do")
	public List<FunctionMenu> getOrganList(
			@RequestParam(value = "parentId", required = false) Integer parentId,
			HttpServletRequest request, HttpServletResponse response) {
    	
		FunctionMenu functionMenu = new FunctionMenu();		
    	if (parentId != null){
    		functionMenu.setParentId(parentId);
		}else {
			functionMenu.setParentId(0);
		}
		//获取根节点
		List<FunctionMenu> list = functionService.getFunctionMenuByParentId(functionMenu);
	
//		for(Organ a:list){
//			a.setName(a.getName());
//			if(a.getChildrenCount() > 0){ 
//				a.setState("closed"); 
//			}else{
//				a.setChildren(new ArrayList<Organ>());
//				a.setState("open");
//			}
//		}
		//加载子节点，方式一，无子节点则无展开按钮
		for(FunctionMenu a:list){
			a.setText(a.getName()); //设置根节点的名称
			FunctionMenu fun = new FunctionMenu();
			fun.setParentId(a.getId());
			List<FunctionMenu> list1 = new ArrayList<FunctionMenu>();//获取子节点的集合
			list1 = functionService.getFunctionMenuByParentId(fun);
			if(list1.size() > 0){
				/*for(FunctionMenu f: list1){
					f.setText(f.getName());
				}
				a.setChildren(list1);
				a.setState("open");*/
				list1 = setChildren(list1);
			}/*else{
				a.setChildren(new ArrayList<FunctionMenu>());
				a.setState("open");
			}*/
			a.setChildren(list1);
			a.setState("open");
		}
		//加载子节点，方式二，无子节点仍有展开按钮，加载速度快
//		if(list.size() > 0){
//			for(Organ a:list){
//				a.setText(a.getName());
//				a.setState("closed");
//			}
//		}
		return list;// json.toString();
	}
	
	private List<FunctionMenu> setChildren(List<FunctionMenu> ls) {
		// TODO Auto-generated method stub
		for (FunctionMenu c : ls) {
			c.setText(c.getName());
			FunctionMenu c1 = new FunctionMenu();
			c1.setParentId(c.getId());
			List<FunctionMenu> lst = functionService.getFunctionMenuByParentId(c1);
			if (lst.size() > 0) {
				lst = setChildren(lst);
				c.setChildren(lst);
				c.setState("open");
			} else {
				c.setChildren(new ArrayList<FunctionMenu>());
				c.setState("open");
			}
		}
		return ls;// json.toString();
	}

}
