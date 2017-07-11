package com.rmbank.test;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test; 

import com.google.common.base.Preconditions;
import com.google.common.base.Strings; 
public class EndecryptUtilTest {
	
	
	@Test
	public void main(){
		endecrypt();
	}  
	private void endecrypt() {
		
		
		//这里以自贡中支为例
		
		for(int i = 1;i<25;i++){
			if(i <10){
				Preconditions.checkArgument(!Strings.isNullOrEmpty("zgzzjg0"+i),"username不能为空");
	        }else{
				Preconditions.checkArgument(!Strings.isNullOrEmpty("zgzzjg"+i),"username不能为空");
	        }
	        
	        Preconditions.checkArgument(!Strings.isNullOrEmpty("123456"),"password不能为空");
	        SecureRandomNumberGenerator secureRandomNumberGenerator=new SecureRandomNumberGenerator();
	        String salt= secureRandomNumberGenerator.nextBytes().toHex();
	        //组合username,两次迭代，对密码进行加密
	        String password_cipherText= new Md5Hash("123456","zgzzjg01"+salt,2).toHex();
	
	        if(i <10){
	        	System.out.println("XX中支结构：0"+i);
	        }else{

	        	System.out.println("XX中支结构："+i);
	        }
	        if(i <10){
	        	System.out.println("account: zgzzjg0"+i);
	        }else{
	        	System.out.println("account: zgzzjg"+i);
	        }
	        
	        System.out.println("salt:"+salt);
	        System.out.println("password:"+password_cipherText);
		}
	}
}