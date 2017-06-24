package com.rmbank.supervision.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 鏂囦欢宸ュ叿锟�渚濊禆涓巗pirng锟�/br> 
 * spring涓殑xml锟�锟斤拷閰嶇疆</br>
 *&lt;bean id="multipartResolver"  
 *       class="org.springframework.web.multipart.commons.CommonsMultipartResolver" 
 *       p:defaultEncoding="utf-8"/&gt;
 * @author dengbin
 *
 */
public class FileUtil {

	protected final Log log = LogFactory.getLog(getClass());
	
	/**瑙夊緱璺緞*/
	public static final int ABSOLUTE_PATH=1;
	/**鐩稿璺緞*/
	public static final int RELATIVELY_PATH=2;

	/**
	 * 淇濆瓨瀵硅薄鍒扮鐩樻枃浠朵笂
	 * @param path
	 * @param bytes
	 */
	public void saveFile(String path,byte[] bytes){
		
		File file = new File(path);
		OutputStream os = null;
		try{
			os = new FileOutputStream(file);
			os.write(bytes);
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			try {
				os.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}
	
	/**
	 * 鍗曟枃浠朵笂锟�
	 * @param request
	 * @param path
	 * @throws IOException
	 */
	public static String uploadSingleFile(HttpServletRequest request, String saveName, String path, int pathType) throws IOException{
		System.out.println("锟�锟斤拷涓婁紶");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest
        .getFile("file");  
		String realFileName = file.getOriginalFilename();  
		if(saveName==null||"".equals(saveName))
			saveName = multipartRequest.getParameter("name");  
		if(saveName==null||"".equals(saveName))
			saveName=realFileName;
		String savePath="";
		if(pathType==FileUtil.RELATIVELY_PATH)
			savePath = request.getSession().getServletContext().getRealPath(  
        "/")+path; 
		else
			savePath=path;
		File dirPath = new File(savePath);  
		if (!dirPath.exists()) {  
		    dirPath.mkdir();  
		}  
		File uploadFile = new File(savePath +"\\"+ saveName);  
		FileCopyUtils.copy(file.getBytes(), uploadFile);
		request.setAttribute("files", loadFiles(request,path)); 
		return saveName;
	}
	
	/**
	 * 澶氭枃浠朵笂锟�
	 * @param request
	 * @param path
	 * @throws IOException
	 */
	public static void uploadMultiFile(HttpServletRequest request, String path, int pathType) throws IOException{
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        String savePath="";
        if(pathType==FileUtil.RELATIVELY_PATH)
	        savePath = request.getSession().getServletContext().getRealPath(  
	                "/")+path;
        else
        	savePath=path;
  
        File file = new File(savePath);  
        if (!file.exists()) {  
            file.mkdir();  
        }  
        String fileName = null;  
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile mf = entity.getValue();
            fileName = mf.getOriginalFilename();  
            File uploadFile = new File(savePath + fileName);  
            FileCopyUtils.copy(mf.getBytes(), uploadFile);
        }  
        request.setAttribute("files", loadFiles(request,path));  
	}
	
	public void downloadFile(){
		
	}
	
	/**
	 * 鏂囦欢淇濆瓨璺緞
	 * @param request
	 * @return
	 */
    public static List<String> loadFiles(HttpServletRequest request, String path) {
        List<String> files = new ArrayList<String>();  
        String ctxPath = request.getSession().getServletContext().getRealPath(  
                "/")+path;  
        File file = new File(ctxPath);  
        if (file.exists()) {  
            File[] fs = file.listFiles();  
            String fname = null;  
            for (File f : fs) {  
                fname = f.getName();  
                if (f.isFile()) {  
                    files.add(fname);  
                }  
            }  
        }  
        return files;  
    } 
    
    /**
     * 鏂囦欢涓嬭浇
     * @param request
     * @param response
     * @param url 鏂囦欢璺緞
     * @param pathType 璺緞绫诲瀷
     * @throws Exception
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, String url, int pathType) throws Exception{
        response.setContentType("text/html;charset=utf-8");  
        request.setCharacterEncoding("UTF-8");  
        BufferedInputStream bis = null;  
        BufferedOutputStream bos = null;  
        String path="";
        if(pathType==FileUtil.RELATIVELY_PATH)
        	path = request.getSession().getServletContext().getRealPath(  
                "/")  
                + url;  
        else
        	path=url;
        try {
        	String fileName = url.substring(url.lastIndexOf("/")+1, url.length());
            long fileLength = new File(path).length();  
            response.setContentType("application/x-msdownload;");  
            response.setHeader("Content-disposition", "attachment; filename="  
                    +  java.net.URLEncoder.encode(fileName, "UTF-8"));  
            response.setHeader("Content-Length", String.valueOf(fileLength));  
            bis = new BufferedInputStream(new FileInputStream(path));  
            bos = new BufferedOutputStream(response.getOutputStream());  
            byte[] buff = new byte[2048];  
            int bytesRead;  
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
                bos.write(buff, 0, bytesRead);  
            }  
        } catch (Exception e) {  
			try {
				PrintWriter out;
				out = response.getWriter();
				if(e instanceof FileNotFoundException){
					out.write("鏂囦欢涓嶅瓨鍦紒");
				}else{
					out.write(e.getMessage());
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        } finally {  
            if (bis != null)  
                bis.close();  
            if (bos != null)  
                bos.close();  
        }  
    }
}
