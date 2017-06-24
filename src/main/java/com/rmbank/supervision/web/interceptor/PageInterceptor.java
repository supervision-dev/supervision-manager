package com.rmbank.supervision.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.model.User;
import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
 

public class PageInterceptor implements HandlerInterceptor {

	private List<String> allowAction;

	private String janusAction;
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod method = (HandlerMethod) handler;
		Logger log = Logger.getLogger(method.getClass());
		
		
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = requestUri.substring(contextPath.length());
		log.info("PageInterceptor [" + method.getBean() +":"+url+ " Started...]");
		log.info(" ");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerMethod h =  (HandlerMethod)handler;
		Logger log =Logger.getLogger(h.getClass());
		
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = requestUri.substring(contextPath.length());
		
		request.setAttribute("requestCurrURL", url.substring(url.lastIndexOf("/")+1));
		
		log.info("PageInterceptor [" + h.getBean() +":"+url+ " Normally End.]");
	}

	public List<String> getAllowAction() {
		return allowAction;
	}

	public void setAllowAction(List<String> allowAction) {
		this.allowAction = allowAction;
	}
	public String getJanusAction() {
		return janusAction;
	}

	public void setJanusAction(String janusAction) {
		this.janusAction = janusAction;
	}
}
