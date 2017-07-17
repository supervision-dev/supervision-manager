package com.rmbank.supervision.common.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;

import com.rmbank.supervision.common.StatisticModelList;

public class ExportExcel {
	public static boolean Export(String fileName, String title, String orgNmae,
			StatisticModelList sml,HttpServletResponse response) {
		boolean state = false;

		// 声明一个工作薄，Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();

		// 声明一个单子并命名（即Excel表格右下方选项的名称，没有命名则使用默认的）
		// HSSFSheet sheet = wb.createSheet("表格一");
		HSSFSheet sheet = wb.createSheet();

		// 生成一个样式
		HSSFCellStyle style = wb.createCellStyle();
		// 样式字体居中
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// 合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		// 创建第一行（也可以称为表头）
		HSSFRow row = sheet.createRow(0);
		// 给表头第一行依次创建单元格
		HSSFCell cell = row.createCell(0);
		cell.setCellStyle(style);
		cell.setCellValue(title);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("导出时间：" + Constants.DATE_FORMAT.format(new Date()));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 6));
		cell = row.createCell(3);
		cell.setCellValue("立项机构：" + orgNmae);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		cell.setCellStyle(style1);
		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue("所属机关");
		cell = row.createCell(1);
		cell.setCellValue("机构名称");
		cell = row.createCell(2);
		cell.setCellValue("项目总数");
		cell = row.createCell(3);
		cell.setCellValue("已完成");
		cell = row.createCell(4);
		cell.setCellValue("未完成");
		cell = row.createCell(5);
		cell.setCellValue("逾期未完成");
		cell = row.createCell(6);
		cell.setCellValue("逾期完成");

		// 添加一些数据，这里先写死
		// List<String> list = new ArrayList<String>();
		// list.add(new String("张三"));
		// list.add(new String("李四"));
		// list.add(new String("王五"));
		//
		// 向单元格里填充数据
		int rowIndex = 3;
		int endindex=0;
		for (short i = 0; i < sml.getTotalList().size(); i++) {
			row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(sml.getTotalList().get(i).getOrgParentName());
			row.createCell(1).setCellValue(sml.getTotalList().get(i).getOrgName());
			row.createCell(2).setCellValue(sml.getTotalList().get(i).getTotalCount());
			row.createCell(3).setCellValue(sml.getTotalList().get(i).getComCount());
			row.createCell(4).setCellValue(sml.getTotalList().get(i).getUnComCount());
			row.createCell(5).setCellValue(sml.getTotalList().get(i).getOverUnComCount());
			row.createCell(6).setCellValue(sml.getTotalList().get(i).getOverComCount());
			endindex++;
		}
		sheet.addMergedRegion(new CellRangeAddress(endindex+3, endindex+3, 0, 1));
		row = sheet.createRow(endindex+3);
		cell = row.createCell(0);
		cell.setCellValue("效能监察项目");
		//用于添加后面单元格
//		for(){
//			
//		}
		sheet.addMergedRegion(new CellRangeAddress(endindex+4, endindex+4, 0, 1));
		row = sheet.createRow(endindex+4);
		cell = row.createCell(0);
		cell.setCellValue("实时监察项目");
		//用于添加后面单元格
//		for(){
//			
//		}
		
		try {
			// 默认导出到D盘下
			//FileOutputStream out = new FileOutputStream("d://" + fileName+ ".xls");
			ServletOutputStream out = response.getOutputStream();
			
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=details.xls");
            response.setContentType("application/x-download");
          		
			wb.write(out);
			out.close();
			state = true;
			System.out.println("导出成功!");
			// JOptionPane.showMessageDialog(null, "导出成功!");
		} catch (FileNotFoundException e) {
			// JOptionPane.showMessageDialog(null, "导出失败!");
			System.out.println("导出失败!");
			e.printStackTrace();
		} catch (IOException e) {
			// JOptionPane.showMessageDialog(null, "导出失败!");
			System.out.println("导出失败!");
			e.printStackTrace();
		}
		return state;
	}
	
}
