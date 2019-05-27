package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.EmployeeBaseMapper;
import com.luying.weixinSystem.mapper.WxOpenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WxOpenService {
    @Autowired
    private WxOpenMapper wxOpenMapper;
    @Autowired
    private EmployeeBaseMapper employeeBaseMapper;

    public String queryMobile(String openid) throws Exception{
        Map<String,String> map = new HashMap<>();
        map.put("openid",openid);
        return wxOpenMapper.queryMobile(map);
    }

    public void insert(Map map) throws Exception{
        wxOpenMapper.add(map);
    }
    public List<Map<String,String>> queryForList(Map map) throws Exception{
        return wxOpenMapper.queryForList(map);
    }
    @Transactional
    public void binding(Map map) throws Exception{
        map.put("bindingState","1");
        wxOpenMapper.update(map);
        employeeBaseMapper.updateOne(map);
    }
    @Transactional
    public void unBinding(Map map) throws Exception{
        map.put("bindingState","0");
        wxOpenMapper.updateByMobile(map);
        employeeBaseMapper.updateOne(map);
    }
}
