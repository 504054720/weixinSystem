package com.luying.weixinSystem.mapper;

import java.util.List;
import java.util.Map;

public interface SysPermissionMapper {
    List<Map<String,String>> queryList(Map map);
}
