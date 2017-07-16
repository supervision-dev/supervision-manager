package com.rmbank.test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.rmbank.supervision.common.utils.ExcelUtil;
import com.rmbank.supervision.common.utils.ExportExcel;

public class ExportTest {
	@Test
	public void main() throws Exception{
		/*ExportExcel ee= new ExportExcel();
		ee.Export("文件名");
		*/
		List<Student> data =new ArrayList<Student>();
		Student stu = new Student("张三",19,"男");
		Student stu1 = new Student("张二",18,"女");
		Student stu2 = new Student("张无",17,"男");
		data.add(stu);
		data.add(stu1);
		data.add(stu2);
		OutputStream out = new FileOutputStream("d://student.xls");
		Map<String , String> fields = new LinkedHashMap<String, String>();
		fields.put("name", "姓名");
		fields.put("age", "年龄");
		fields.put("sex", "性别");
		ExcelUtil.ListToExcel(data, out, fields);
	}
}
