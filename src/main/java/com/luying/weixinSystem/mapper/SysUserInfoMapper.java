package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface SysUserInfoMapper {
    Map<String,String> query(Map map);
    List<Map<String,String>> queryList(Map map) throws Exception;
    void insert(Map map) throws Exception;
    Map<String,String> queryOne(String id) throws Exception;
    void update(Map map) throws Exception;
    void delete(String userId) throws Exception;
    List<Map<String,String>> queryMenuList(Map map);
    Map<String,String> queryInfo(String username) throws Exception;
}
