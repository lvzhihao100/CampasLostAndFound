package com.edu.claf.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具
 */
public class MD5Utils {

    /**
     * 对密码进行MD5加密
     * @param password
     * @return
     */
    public static String encode(String password){
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b: digest) {
                int i = b & 0xff;//获取字节的低八位的有效值
                String hexString = Integer.toHexString(i);//将整数转换为16进制
                if (hexString.length() < 2){
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return  sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //没有算法抛出异常
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 获取到文件的MD5值（病毒特征码）
     * @param sourceDir
     * @return
     */
    public static String getFileMD5(String sourceDir) {
        File file = new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = -1;
            //获取到数字摘要
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while ((len = fis.read(bytes))  != -1 ){
                md5.update(bytes,0,len);
            }
            byte[] result = md5.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b: result) {
                int i = b & 0xff;//获取字节的低八位的有效值
                String hexString = Integer.toHexString(i);//将整数转换为16进制
                if (hexString.length() < 2){
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return  sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
