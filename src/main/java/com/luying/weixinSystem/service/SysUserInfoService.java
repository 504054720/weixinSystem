package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.SysUserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysUserInfoService {
    @Autowired
    SysUserInfoMapper sysUserInfoMapper;

    public Map<String,String> query(Map map){
        return sysUserInfoMapper.query(map);
    }
    public Map<String,String> queryInfo(String username) throws Exception{
        return sysUserInfoMapper.queryInfo(username);
    }
}
