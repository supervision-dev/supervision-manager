package com.rmbank.supervision.web.controller.base;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rmbank.supervision.web.controller.SystemAction;


@Scope("prototype")
@Controller
@RequestMapping("/pageRoute")
public class PageRouteController extends SystemAction {
	/**
     * 用户管理列表
     *
     * @param request
     * @param response
     * @return
	 * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/system/user/userList.do")
    public ModelAndView userList( HttpServletRequest request, 
    		HttpServletResponse response) throws UnsupportedEncodingException {  
    	ModelAndView mv = new ModelAndView("web/base/user/userList");
    	return mv;
    }
}
