package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface SysRoleMapper {
    Map<String,String> queryOne(Map map) throws Exception;
    void insert(Map map) throws Exception;
    List<Map<String,String>> queryList(Map map) throws Exception;
    List<Map<String,String>> queryPermissionByRole(String id) throws Exception;
    void delPermissionByRole(String id) throws Exception;
    void insertRolePermission(List list) throws Exception;
    void update(Map map) throws Exception;
    void delete(String id) throws Exception;
}
