package com.luying.weixinSystem.util;

import org.apache.commons.lang3.RandomUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类
 */
public class EncryptionUtil {
    private EncryptionUtil(){}
    private static final String ALGORITHM = "DES";
    private static final String SECRET_KEY = "wxluying";

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionUtil.class);

    /**
     * Get Des Key
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
        SecretKey deskey = keygen.generateKey();
        return deskey.getEncoded();
    }

    /**
     * 加密
     * @param input
     * @param key
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encode(byte[] input, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key, ALGORITHM);
        Cipher c1 = Cipher.getInstance(ALGORITHM);
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        return c1.doFinal(input);
    }

    /**
     * 解密
     * @param input
     * @param key
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception
     */
    public static byte[] decode(byte[] input, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key, ALGORITHM);
        Cipher c1 = Cipher.getInstance(ALGORITHM);
        c1.init(Cipher.DECRYPT_MODE, deskey);
        return c1.doFinal(input);
    }

    /**
     *
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] md5(byte[] input) throws NoSuchAlgorithmException  {
        java.security.MessageDigest alg = java.security.MessageDigest
                .getInstance("MD5"); // or "SHA-1"
        alg.update(input);
        return alg.digest();
    }

    /**
     * Convert byte[] to String
     * @param  b
     * @return String
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs =new StringBuilder();
        String stmp ;
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append("0" + stmp) ;
            } else {
                hs.append( stmp) ;
            }
        }
        return hs.toString().toUpperCase();
    }

    /**
     * Convert String to byte[]
     * @param hex
     * @return byte[]
     */
    public static byte[] hex2byte(String hex)  {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap =  Character.toString(arr[i++]) + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            Integer obj = new Integer(byteint);
            b[j] = obj.byteValue();
        }
        return b;
    }

    public static String encode(String value) throws Exception{
        return byte2hex(EncryptionUtil.encode(value.getBytes(),SECRET_KEY.getBytes()));
    }
    public static String decode(String value) throws Exception{
        return new String(EncryptionUtil.decode(EncryptionUtil.hex2byte(value), SECRET_KEY.getBytes()));
    }

    /**
     * 获得随机盐
     * @return
     */
    public static String getSalt(){
        String hashAlgorithmName = "MD5";
        String credentials = Integer.toString(RandomUtils.nextInt(0,1024));
        int hashIteration = 1;
        Object obj = new SimpleHash(hashAlgorithmName,credentials,"",hashIteration);
        return obj.toString();
    }

    /**
     * 获得加密后的密码
     * @param username
     * @param password
     * @param salt
     * @return
     */
    public static String getPassword(String username,String password,String salt){
        String hashAlgorithmName = "MD5";
        int hashIteration = 2;
        Object obj = new SimpleHash(hashAlgorithmName,password,username + salt,hashIteration);
        return obj.toString();
    }

    /**
     * 测试
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)  {
        System.out.println(getSalt());
        System.out.println(getPassword("admin","luying@201904","e01010e9c000570fc4105a74cf7d1344"));

//		try {
//		    //加密
//	        LOGGER.info(byte2hex(encode("10000.5".getBytes(),SECRET_KEY.getBytes())));
//	        //解密
//	        LOGGER.info( new String(EncryptionUtil.decode(EncryptionUtil.hex2byte("A517F3E6DEA31559"), SECRET_KEY.getBytes())));
//        } catch (Exception e) {
//	     LOGGER.error("", "", e);
//        }
        //dd3736d1c3c4702e5170cde08bffd411
        //d3c59d25033dbf980d29554025c23a75
        //d3c59d25033dbf980d29554025c23a75


    }
}
