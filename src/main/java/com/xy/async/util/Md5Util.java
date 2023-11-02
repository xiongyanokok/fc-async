package com.xy.async.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 * 
 * @author xiongyan
 * @date 2019/09/19
 */
public class Md5Util {

    private Md5Util() {
    }

    /**
     * Used building output as Hex
     */
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 对字符串进行MD5加密
     *
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text) {
        MessageDigest mdInst;
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            mdInst = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        // 使用指定的字节更新摘要
        mdInst.update(text.getBytes());
        // 获得密文
        byte[] mds = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        int j = mds.length;
        char[] str = new char[j * 2];
        int k = 0;
        for (byte md : mds) {
            str[k++] = DIGITS[md >>> 4 & 0xf];
            str[k++] = DIGITS[md & 0xf];
        }
        return new String(str);
    }

}
