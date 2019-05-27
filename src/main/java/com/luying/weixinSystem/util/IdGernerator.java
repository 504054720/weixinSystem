package com.luying.weixinSystem.util;

import com.relops.snowflake.Snowflake;
import org.apache.commons.lang3.RandomUtils;

public class IdGernerator {
    private IdGernerator(){}
    private static Snowflake s = new Snowflake(RandomUtils.nextInt(0,1024));
    public static String next(){
        return String.valueOf(s.next());
    }
}
