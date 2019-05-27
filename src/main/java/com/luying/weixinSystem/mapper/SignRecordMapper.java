package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface SignRecordMapper {
    void insert(Map map) throws Exception;
    Map<String,String> queryOne(Map map) throws Exception;
    void update(Map map) throws Exception;
    List<Map<String,String>> queryAllYesterday(Map map) throws Exception;

    String queryDayStatus(Map map) throws Exception;
    void updateState(Map map) throws Exception;
    List<Map<String,Object>> queryRecordByMonth(Map map) throws Exception;
}
