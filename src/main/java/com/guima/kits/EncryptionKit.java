package com.guima.kits;

import java.security.MessageDigest;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionKit {

    // 这是默认模式
    //  public static final String transformation = "AES/ECB/PKCS5Padding";
    // 使用CBC模式, 在初始化Cipher对象时, 需要增加参数, 初始化向量IV : IvParameterSpec iv = new IvParameterSpec(key.getBytes());
    //  public static final String transformation = "AES/CBC/PKCS5Padding";
    // NOPadding: 使用NOPadding模式时, 原文长度必须是8byte的整数倍
//    AES/CBC/NOPadding
    public static final String transformation = "AES/ECB/PKCS5Padding";
    public static final String miniProgramkey = "wyaominiprogram5";
    public static final String algorithm = "AES";

    public static String md5Encrypt(String srcStr){
        return encrypt("MD5", srcStr);
    }

    public static String sha1Encrypt(String srcStr){
        return encrypt("SHA-1", srcStr);
    }

    public static String sha256Encrypt(String srcStr){
        return encrypt("SHA-256", srcStr);
    }

    public static String sha384Encrypt(String srcStr){
        return encrypt("SHA-384", srcStr);
    }

    public static String sha512Encrypt(String srcStr){
        return encrypt("SHA-512", srcStr);
    }

    public static String encrypt(String algorithm, String srcStr) {
        try {
            StringBuilder result = new StringBuilder();
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(srcStr.getBytes("utf-8"));
            for (byte b :bytes) {
                String hex = Integer.toHexString(b&0xFF);
                if (hex.length() == 1)
                    result.append("0");
                result.append(hex);
            }
            return result.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES 加密
     * @param original
     * @return
     * @throws Exception
     */
    public static String encryptByAES(String original) throws Exception {

        // 获取Cipher
        Cipher cipher = Cipher.getInstance(transformation);
        // 生成密钥
        SecretKeySpec keySpec = new SecretKeySpec(miniProgramkey.getBytes(), algorithm);
        // 指定模式(加密)和密钥
//        // 创建初始化向量
//        IvParameterSpec iv = new IvParameterSpec(key.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        //cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        // 加密
        byte[] bytes = cipher.doFinal(original.getBytes());

        return Base64.encode(bytes);
    }

    /**
     * AES 解密
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static String decryptByAES(String encrypted){
        try {
            // 获取Cipher
            Cipher cipher = Cipher.getInstance(transformation);
            // 生成密钥
            SecretKeySpec keySpec = new SecretKeySpec(miniProgramkey.getBytes(), algorithm);
            // 指定模式(解密)和密钥
            // 创建初始化向量
//        IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            //  cipher.init(Cipher.DECRYPT_MODE, keySpec);
            // 解密
            byte[] bytes = cipher.doFinal(Base64.decode(encrypted));
            return new String(bytes);
        }catch (Exception e){
            return "";
        }
    }

    public static void main(String[] args) throws Exception {

        String encryptByAES = encryptByAES("");
        System.out.println(encryptByAES);
        String decryptByAES = decryptByAES(encryptByAES);
        System.out.println(decryptByAES);

    }
}
