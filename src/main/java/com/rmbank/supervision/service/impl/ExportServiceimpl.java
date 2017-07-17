package com.rmbank.supervision.service.impl;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.rmbank.supervision.common.StatisticModelList;
import com.rmbank.supervision.common.utils.ExportExcel;
import com.rmbank.supervision.service.ExportService;

@Service("exportService")
public class ExportServiceimpl implements ExportService {

	@Override
	public boolean export(String fileName, String title, String orgNmae,
			StatisticModelList sml,HttpServletResponse response) {
		boolean state = ExportExcel.Export(fileName, title, orgNmae, sml,response);
		return state;
	}

}
