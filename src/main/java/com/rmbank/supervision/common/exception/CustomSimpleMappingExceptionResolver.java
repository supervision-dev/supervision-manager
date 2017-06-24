package com.rmbank.supervision.common.exception;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomSimpleMappingExceptionResolver extends SimpleMappingExceptionResolver
{
  protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
  {
    String viewName = determineViewName(ex, request);
    if (viewName != null) {
      String h = request.getHeader("accept");
      String b = request.getHeader("X-Requested-With");
      if ((request.getHeader("accept").indexOf("application/json") <= -1) && (
        (request
        .getHeader("X-Requested-With") == null) || 
        (request
        .getHeader("X-Requested-With").indexOf("XMLHttpRequest") <= -1)))
      {
        Integer statusCode = determineStatusCode(request, viewName);
        if (statusCode != null) {
          applyStatusCodeIfPossible(request, response, statusCode.intValue());
        }
        return getModelAndView(viewName, ex, request);
      }
      try {
        PrintWriter writer = response.getWriter();
        writer.write(ex.getMessage());
        writer.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    return null;
  }
}