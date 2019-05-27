package com.luying.weixinSystem.util;

import com.relops.snowflake.Snowflake;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /***
     * 验证是否为手机号
     * @param str
     * @return
     */
    public static boolean isMobile(final String str) {
        Pattern p;
        Matcher m;
        boolean b;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
    public static String getSquenceByUUID(){
        return UUID.randomUUID().toString();
    }
    public static String getSquenceBySnow(){
        Snowflake snowflake = new Snowflake(1024);
        return String.valueOf(snowflake.next());
    }
    public static void main(String[] args) {
        System.out.println(getSquenceByUUID());
        System.out.println(getSquenceByUUID());
        System.out.println(getSquenceByUUID());

        for (int i = 0; i < 10; i++) {
            System.out.println(IdGernerator.next());
            System.out.println(IdGernerator.next());
            System.out.println(IdGernerator.next());
        }
    }
}
