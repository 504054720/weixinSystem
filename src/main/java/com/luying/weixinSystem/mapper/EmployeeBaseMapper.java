package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface EmployeeBaseMapper {
    List<Map<String,Object>> queryAll(Map map) throws Exception;
    Map<String,String> query(Map map) throws Exception;
    void insert(Map map) throws Exception;
    int queryForValidate(Map map) throws Exception;
    void updateByMobile(Map map) throws Exception;
    Map<String,String> queryBaseInfo(Map map) throws Exception;
    Map<String,String> queryOne(Map map) throws Exception;
    void updateOne(Map map) throws Exception;
    void del(String id) throws Exception;
    List<Map<String,String>> queryForList(Map map) throws Exception;
}
