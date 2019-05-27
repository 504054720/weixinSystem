package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface PayInfoMapper {
    void insert(List list) throws Exception;
    int queryForValidate(Map map);
    List<Map<String,String>> queryYearMonth(Map map);
    List<Map<String,String>> querySalaryInfo(Map map);
}
