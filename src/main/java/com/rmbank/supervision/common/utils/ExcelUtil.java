package com.rmbank.supervision.common.utils;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil {
	private static int sheetSize = 5000; //单个sheet存储的数据
	/**
	 * 
	 * @param data 要导出的数据
	 * @param out 通过输出流导出到磁盘
	 * @param showName 实体类的属性名对应的Excel表格的中文名
	 * @throws Exception 
	 */
	public static <T> void ListToExcel(List <T> data,
			OutputStream out,Map<String, String> showName) throws Exception{
		if(data  == null || data.size() == 0){
			throw new Exception("系统未查询到相关数据！");
		}else {
			//创建Excel文件     
			HSSFWorkbook wb = new HSSFWorkbook();
			//1、计算出一共有多少个sheet
			int sheetNum = data.size()/sheetSize;
			if(data.size()%sheetSize != 0){
				sheetNum+=1;
			}
			//将实体类的属性名和中文名分别存在两个数组中
			String[] attrNames =new String [showName.size()];
			String[] chNames =new String [showName.size()];
			int count = 0;
			for(Entry<String,String> entry:showName.entrySet()){
				String attrName= entry.getKey();
				String chName=entry.getValue();
				attrNames[count]=attrName;
				chNames[count]=chName;
				count++;
			}
			//填充数据
			for(int i=0;i<sheetNum;i++){
				int rowCount = 0;//0-4999 5000-9999 。。。。。
				HSSFSheet sheet = wb.createSheet(); 
				int satrtIndex = i*sheetSize;
				int endIndex = (i+1)*sheetSize-1>data.size()?data.size():(i+1)*sheetSize-1;
				HSSFRow row = sheet.createRow(rowCount);
				//标题行
				for(int j = 0;j<chNames.length;j++){
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(chNames[j]);//设置中文标题
				}
				rowCount++;
				for(int index = satrtIndex; index < endIndex; index++){
					T item = data.get(index);//获取数据的实体对象
					row = sheet.createRow(rowCount);
					//通过反射取值
					for(int j = 0;j<chNames.length;j++){
						Field field = item.getClass().getDeclaredField(attrNames[j]);
						field.setAccessible(true);
						Object o= field.get(item);
						String value =o == null?"":o.toString();
						//String value =o.toString();
						HSSFCell cell = row.createCell(j);
						cell.setCellValue(value);
					}
					rowCount++;
				}
				wb.write(out);
			}
			out.close();
			//2、
		}
		
	}
}
