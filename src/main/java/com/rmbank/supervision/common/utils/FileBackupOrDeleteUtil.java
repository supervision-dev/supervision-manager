package com.rmbank.supervision.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 类描述：文件备份和文件删除工具类。	
 * @author 蒋圣强
 * @date 2018/01/17
 *
 */
public class FileBackupOrDeleteUtil {
	private static Log logger = LogFactory.getLog(FileBackupOrDeleteUtil.class);
	
	/**
	 * 工程的文件目录（需要备份的文件目录）
	 */
	private static final String PROJECT_FILE_PATH="D:\\software\\apache-tomcat-9.0.1\\webapps\\supervision\\source\\uploadfile";
	
	/**
	 * 存放备份文件的目录
	 */
	private static final String BACKUP_FILE_PATH="D:\\backup_directory";
	
	/**
	 * 备份文件保存的天数
	 */
	private static final int DAY_NUMBER=365;
	
	/**
	 * 正在执行文件删除的目录下的文件个数（用于在删除该目录下文件时判断是否是最后一个文件，<br>
	 * 如果是最后一个文件，则删除该文件后需要把该目录一并删除）
	 */
	private static int FILE_NUMBER=0;
	
	/**
	 * 备份指定目录下的文件到另一个目录
	 * @param path 需要备份的文件目录
	 * @param copyToThePath 存放备份文件的目录
	 */
	private static void copyNew(String path,String copyToThePath){
		File file=new File(path);
		String [] filenames=file.list();
		for(String filename:filenames){
			//重新构造新的路径
			String filepath=path+"\\"+filename;
			//在新的路径下重构File
			File f=new File(filepath);
			//判断是否是文件
			if(f.isFile()){
				//前两个"\"是转义字符，而真正的分隔符是"\\"
				String[] split = f.getParent().split("\\\\");
				//执行复制文件操作	
				copy(filepath,copyToThePath+"\\"+split[split.length-1]);	
			}else if(f.isDirectory()){
				//如果是目录，需要在备份目录下构造相同的文件目录
				String mkdir_path=copyToThePath+"\\"+filename;
				File mkdirfile=new File (mkdir_path);
				if(mkdirfile.exists()==false){
					mkdirfile.mkdir();
				}else{
				}
				copyNew(filepath,copyToThePath);
			}	
		}
	}
	
	
	
	/**
	 * 创建每天备份的目录
	 */
	private static String createFolder(){
		//构建每天备份的目录
		String mkdir_path=BACKUP_FILE_PATH+"\\"+DateUtil.format(new Date())+"#"+UUID.randomUUID();
		File mkdirfile=new File (mkdir_path);
		if(mkdirfile.exists()==false){
			//该目录不存在则直接构建
			mkdirfile.mkdir();
			return mkdir_path;
		}else{
			//如果该目录存在，则重新构建
			return createFolder();
		}
	}
	
	/**
	 * 备份文件单个文件
	 * @param path	需要备份的文件的地址
	 * @param copyToThePath	存放备份文件的地址
	 */
	private static void copy(String path,String copyToThePath){
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try{
			//读取原始文件到内存
			File file =new File(path);
			fis=new FileInputStream(file);
			byte [] b =new byte [(int)file.length()];
			fis.read(b);
			
			//从内存中把原始文件输出到新的目录
			File newfile=new File(copyToThePath,file.getName());
			fos=new FileOutputStream(newfile);
			fos.write(b);
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}finally{
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	} 
	
	/**
	 * 备份当前时间以前的所有上传的文件
	 */
	public static void backUpBeforeFile(){
		try {
			//创建当天备份的文件目录
			String copyToThePath = createFolder();
			//执行文件备份
			copyNew(PROJECT_FILE_PATH,copyToThePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 删除文件目录
	 * @param path
	 * @throws IOException
	 */
	private static void deleteFile(String path){
		File file=new File(path);
		//声明一个数组将指定路径下的所有文件的名称存入数组里面；
		String []fileNames=file.list();
		FILE_NUMBER=fileNames.length;
		//循环遍历数组里面的文物名称
		for(String fileName:fileNames){
			//在指定路径下拼接文件名称
			String filepath=path+"\\"+fileName;
			//在新的路径下重构File类
			File f=new File(filepath);
			//判断是否是文件 
			if(f.isFile()){//打印出文件名
				if(fileName.endsWith("")){//判断文件后缀名是否以空字符结束
					f.delete();//删除文件
					logger.info("执行了删除过期备份文件！");
					FILE_NUMBER--;
					if(FILE_NUMBER==0){
						File pf= new File(f.getParent());
						pf.delete();
					}
				}
				
			}else if(f.isDirectory()){
				deleteFile(filepath);
			}		
			if(FILE_NUMBER==0){
				File pf= new File(f.getParent());
				pf.delete();
			}
		}
		if(FILE_NUMBER==0){
			File pf= new File(path);
			pf.delete();
		}
	} 
	
	/**
	 * 获取指定天数前的创建的目录
	 * @param day
	 * @return
	 */
	private static List<String> getDateBeforeFile(int day){
		List<String> fileListTemp=new ArrayList<String>();
		Date dateBefore = DateUtil.getDateBefore(new Date(), day);
		String format = DateUtil.format(dateBefore);
		File file=new File(BACKUP_FILE_PATH);
		String [] fileNames=file.list();
		for(String fileName:fileNames){
			String substring = fileName.substring(0, 10);
			int fileDayNumber = Integer.parseInt(substring.replaceAll("\\-", ""));
			int nowDayNumber = Integer.parseInt(format.replaceAll("\\-", ""));
			if(fileDayNumber<nowDayNumber){
				fileListTemp.add(fileName);
			}
		}
		return fileListTemp;
	}
	
	public static void deleteExpireFile(){
		List<String> dateBeforeFile = getDateBeforeFile(DAY_NUMBER);
		for (String string : dateBeforeFile) {
			deleteFile(BACKUP_FILE_PATH+"\\"+string);
		}
	}
	
	public static void main(String[] args) {
		//backUpBeforeFile();
		//deleteExpireFile()
	}
}
