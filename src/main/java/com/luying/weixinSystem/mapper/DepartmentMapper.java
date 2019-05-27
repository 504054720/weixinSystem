package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface DepartmentMapper {
    void insert(Map map) throws Exception;
    Map<String,String> queryOne(Map map) throws Exception;
    List<Map<String,String>> queryList(Map map) throws Exception;
    void update(Map map) throws Exception;
    void delete(String id) throws Exception;
}
