package com.rmbank.supervision.service;

import javax.servlet.http.HttpServletResponse;

import com.rmbank.supervision.common.StatisticModelList;

public interface ExportService {

	boolean export(String fileName,String title,String orgNmae,StatisticModelList sml,HttpServletResponse response);
}
