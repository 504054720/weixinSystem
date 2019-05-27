package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface WxOpenMapper {
    int queryCount(Map map) throws Exception;
    void add(Map map) throws Exception;
    void update(Map map) throws Exception;
    String queryMobile(Map map) throws Exception;
    void delete(String openid) throws Exception;
    List<Map<String,String>> queryForList(Map map) throws Exception;
    void updateByMobile(Map map) throws Exception;
}
