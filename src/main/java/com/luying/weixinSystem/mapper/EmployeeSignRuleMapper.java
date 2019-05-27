package com.luying.weixinSystem.mapper;

import java.util.Map;

public interface EmployeeSignRuleMapper {
    void insert(Map map) throws Exception;
    void update(Map map) throws Exception;
    Map<String,String> query(Map map) throws Exception;
}
