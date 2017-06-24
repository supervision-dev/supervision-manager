package com.rmbank.supervision.web.controller.vision;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.IpUtil;
import com.rmbank.supervision.model.Item;
import com.rmbank.supervision.model.ItemProcess;
import com.rmbank.supervision.model.Organ;
import com.rmbank.supervision.model.User;
import com.rmbank.supervision.service.ItemService;
import com.rmbank.supervision.service.UserService;
import com.rmbank.supervision.web.controller.SystemAction;

/**
 * 效能监察待办事项控制器
 * @author DELL
 *
 */ 

@Scope("prototype")
@Controller
@RequestMapping("/vision")
public class VisionTodoAction  extends  SystemAction {

	@Resource
	private ItemService itemService;

	@Resource
	private UserService userService;

	/**
     * 效能监察待办事项列表展示
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/todoList.do")
    @RequiresPermissions("vision/todoList.do")
    public String efficiencyList(Item item, 
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException { 
    	// 判断搜索名是否为空，不为空则转为utf-8编码
		if (item.getSearchName() != null && item.getSearchName() != "") {
			String searchName = URLDecoder.decode(item.getSearchName(),"utf-8");
			item.setSearchName(searchName);
		}
		// 设置页面初始值及页面大小
		if (item.getPageNo() == null)
			item.setPageNo(1);
		item.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		int totalCountXN = 0;
		int totalCountLZ = 0;
		int totalCountZF = 0;
		
		//获取当前登录用户
		User loginUser = this.getLoginUser();
		//获取当前用户对应的机构列表
		List<Organ> userOrgList=userService.getUserOrgByUserId(loginUser.getId());
		//获取当前用户对应的第一个机构
		Organ userOrg=userOrgList.get(0);
		
		// 分页集合
		List<Item> itemListXN = new ArrayList<Item>();
		List<Item> itemListLZ = new ArrayList<Item>();
		List<Item> itemListZF = new ArrayList<Item>();
		try {
			//成都分行和超级管理员获取所有项目
			if(userOrg.getOrgtype()==Constants.ORG_TYPE_1 ||
					userOrg.getOrgtype()==Constants.ORG_TYPE_2 ||
							userOrg.getOrgtype()==Constants.ORG_TYPE_3 ||
					Constants.USER_SUPER_ADMIN_ACCOUNT.equals(loginUser.getAccount())){
				
				item.setSupervisionTypeId(2); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemListXN = itemService.getItemListXNJCToList(item);		
				totalCountXN = itemService.getItemCountBySSJCDB(item); //实时监察分页
				item.setSupervisionTypeId(3); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemListLZ = itemService.getItemListXNJCToList(item);	
				totalCountLZ = itemService.getItemCountBySSJCDB(item); //实时监察分页
				item.setSupervisionTypeId(4); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemListZF = itemService.getItemListXNJCToList(item);	
			
				totalCountZF = itemService.getItemCountBySSJCDB(item); //实时监察分页
			}else {
				//当前登录用户只加载自己完成的项目				
				item.setSupervisionOrgId(userOrg.getId());
			
				item.setSupervisionTypeId(2); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemListXN = itemService.getItemListToListByLogOrg(item);		
				totalCountXN = itemService.getItemCountToListByLogOrg(item); //实时监察分页
				
				item.setSupervisionTypeId(3); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemListLZ = itemService.getItemListToListByLogOrg(item);	
				totalCountLZ = itemService.getItemCountToListByLogOrg(item); //实时监察分页
				
				item.setSupervisionTypeId(4); //2代表效能监察
				item.setItemType(Constants.STATIC_ITEM_TYPE_SVISION); //实时监察模块
				itemListZF = itemService.getItemListToListByLogOrg(item);	
			
				totalCountZF = itemService.getItemCountToListByLogOrg(item); //实时监察分页
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		// 通过request对象传值到前台
		request.setAttribute("itemListXN", itemListXN);
		request.setAttribute("itemListLZ", itemListLZ);
		request.setAttribute("itemListZF", itemListZF);
		request.setAttribute("totalCountXN", totalCountXN);
		request.setAttribute("totalCountLZ", totalCountLZ);
		request.setAttribute("totalCountZF", totalCountZF);

    	 
    	return "web/vision/todoList";
    }
}
