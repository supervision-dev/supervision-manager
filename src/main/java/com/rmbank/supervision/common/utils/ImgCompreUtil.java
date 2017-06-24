package com.rmbank.supervision.common.utils;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImgCompreUtil {

 	private Image img;  
    private int width;  
    private int height;
    private String savePath;
    private String fileName;
    private File file;
    
    private static BASE64Encoder encoder = new BASE64Encoder();
    
    /** 
     * 鏋勯�鍑芥暟 
     */  
    public ImgCompreUtil(String fileNames,String savePathtemp) throws IOException {
        File file = new File(fileNames);// 璇诲叆鏂囦欢  
        img = ImageIO.read(file);      // 鏋勯�Image瀵硅薄  
        width = img.getWidth(null);    // 寰楀埌婧愬浘瀹� 
        height = img.getHeight(null);  // 寰楀埌婧愬浘闀� 
        savePath = savePathtemp;
        fileName = fileNames;
    }  
    /** 
     * 鏋勯�鍑芥暟 
     */  
    public ImgCompreUtil(File file,String fileNames,String savePathtemp) throws IOException {
    	this.file = file;
        img = ImageIO.read(file);      // 鏋勯�Image瀵硅薄  
        width = img.getWidth(null);    // 寰楀埌婧愬浘瀹� 
        height = img.getHeight(null);  // 寰楀埌婧愬浘闀� 
        savePath = savePathtemp;
        fileName = fileNames;
    }  
    /** 
     * 鎸夌収瀹藉害杩樻槸楂樺害杩涜鍘嬬缉 
     * @param w int 鏈�ぇ瀹藉害 
     * @param h int 鏈�ぇ楂樺害 
     */  
    public void resizeFix(int w, int h) throws IOException {  
        if (width / height > w / h) {  
            resizeByWidth(w);  
        } else {  
            resizeByHeight(h);  
        }  
    }  
    /** 
     * 浠ュ搴︿负鍩哄噯锛岀瓑姣斾緥鏀剧缉鍥剧墖 
     * @param w int 鏂板搴�
     */  
    public void resizeByWidth(int w) throws IOException {  
        int h = (int) (height * w / width);  
        resize(w, h);  
    }  
    /** 
     * 浠ラ珮搴︿负鍩哄噯锛岀瓑姣斾緥缂╂斁鍥剧墖 
     * @param h int 鏂伴珮搴�
     */  
    public void resizeByHeight(int h) throws IOException {  
        int w = (int) (width * h / height);  
        resize(w, h);  
    }
    /** 
     * 鑷姩绛夋瘮渚嬬缉鏀惧浘鐗�
     * @param fas int 鏂伴珮搴�
     */  
    public void resizePIC(boolean fas)throws IOException {  
    	if(fas){
    		BufferedImage sourceImg =ImageIO.read(new FileInputStream(fileName));
       		 int widths = sourceImg.getWidth(); 
       		 int hights = sourceImg.getHeight();
    		 // 涓虹瓑姣旂缉鏀捐绠楄緭鍑虹殑鍥剧墖瀹藉害鍙婇珮搴�
			 double rate1 = ((double) img.getWidth(null)) / (double) widths + 0.1; 
			 double rate2 = ((double) img.getHeight(null)) / (double) hights + 0.1; 
			 // 鏍规嵁缂╂斁姣旂巼澶х殑杩涜缂╂斁鎺у埗 
			 double rate = rate1 > rate2 ? rate1 : rate2; 
			 width = (int) (((double) img.getWidth(null)) / rate); 
			 height = (int) (((double) img.getHeight(null)) / rate); 
    	}
    	resize(width, height);  
    }
    /** 
     * 鑷姩绛夋瘮渚嬬缉鏀惧浘鐗�
     * @param h int 鏂伴珮搴�
     */  
    public void resizeappPIC(boolean fas)throws IOException {  
    	if(fas){
    		BufferedImage sourceImg =ImageIO.read(this.file);
       		 int widths = sourceImg.getWidth(); 
       		 int hights = sourceImg.getHeight();
    		 // 涓虹瓑姣旂缉鏀捐绠楄緭鍑虹殑鍥剧墖瀹藉害鍙婇珮搴�
			 double rate1 = ((double) img.getWidth(null)) / (double) widths + 0.1; 
			 double rate2 = ((double) img.getHeight(null)) / (double) hights + 0.1; 
			 // 鏍规嵁缂╂斁姣旂巼澶х殑杩涜缂╂斁鎺у埗 
			 double rate = rate1 > rate2 ? rate1 : rate2; 
			 width = (int) (((double) img.getWidth(null)) / rate); 
			 height = (int) (((double) img.getHeight(null)) / rate); 
    	}
    	resize(width, height);  
    }
    /** 
     * 寮哄埗鍘嬬缉/鏀惧ぇ鍥剧墖鍒板浐瀹氱殑澶у皬 
     * @param w int 鏂板搴�
     * @param h int 鏂伴珮搴�
     */  
    @SuppressWarnings("restriction")
	public void resize(int w, int h) throws IOException {  
        // SCALE_SMOOTH 鐨勭缉鐣ョ畻娉�鐢熸垚缂╃暐鍥剧墖鐨勫钩婊戝害鐨�浼樺厛绾ф瘮閫熷害楂�鐢熸垚鐨勫浘鐗囪川閲忔瘮杈冨ソ 浣嗛�搴︽參  
        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB );   
//        image.getGraphics().drawImage(img, 0, 0, w, h, null); // 缁樺埗缂╁皬鍚庣殑鍥� 
        image.getGraphics().drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
        File destFile = new File(savePath);  
        FileOutputStream out = new FileOutputStream(destFile); // 杈撳嚭鍒版枃浠舵祦  
        // 鍙互姝ｅ父瀹炵幇bmp銆乸ng銆乬if杞琷pg  
       
		com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(out);  
        encoder.encode(image); // JPEG缂栫爜  
        out.close();  
    }
    /** 
     * 寮哄埗鍘嬬缉/鏀惧ぇ鍥剧墖鍒板浐瀹氱殑澶у皬 
     * @param imagePath 鍥剧墖璺緞
     * @param imageType 鍥剧墖绫诲瀷
     */ 
    @SuppressWarnings("restriction")
	public static String getImageBinary(String imagePath,String imageType){      
        File f = new File(imagePath);             
        BufferedImage bi;      
        try {      
            bi = ImageIO.read(f);      
            ByteArrayOutputStream baos = new ByteArrayOutputStream();      
            ImageIO.write(bi, imageType, baos);      
            byte[] bytes = baos.toByteArray();      
                  
            return encoder.encodeBuffer(bytes).trim();      
        } catch (IOException e) {      
            e.printStackTrace();      
        }      
        return null;    
    }   
    
	public Image getImg() {
		return img;
	}
	public void setImg(Image img) {
		this.img = img;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
