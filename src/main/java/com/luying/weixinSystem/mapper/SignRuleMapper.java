package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface SignRuleMapper {
    void initdays(List list);
    void updatedays(Map p);
    void updatedaystoholiday(List list);
    void updatedaystoweekend(List list);
    List<Map<String,Object>> querydays(Map p);

    Map<String,Object>  queryFirstOneRule(Map p);
    void insertUserRule(Map paraMap) throws Exception;
    Map<String,Object>  queryUserRule(Map paraMap);
    void updatUserRule(Map paraMap);
    List<Map<String,Object>> query(Map paraMap);
    void insert(Map paraMap) throws Exception;
    void update(Map map) throws Exception;
    void delete(Map map) throws Exception;

}
