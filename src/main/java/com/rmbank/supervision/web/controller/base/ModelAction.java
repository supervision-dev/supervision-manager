package com.rmbank.supervision.web.controller.base;

import java.io.UnsupportedEncodingException; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
  

@Scope("prototype")
@Controller
@RequestMapping("/moudle/model")
public class ModelAction {

	
	/**
	 * 配置列表
	 * @param meta
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/modelSelect.do") 
	public String modelSelect(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{ 
		return "modelSelect";
	}
}
