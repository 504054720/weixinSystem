package com.luying.weixinSystem.mapper;

import java.util.Map;

public interface SysUserRoleMapper {
    void insert(Map map) throws Exception;
    void update(Map map) throws Exception;
    void delete(String userId) throws Exception;
}
