package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.*;
import com.luying.weixinSystem.util.EncryptionUtil;
import com.luying.weixinSystem.util.IdGernerator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysAdminService {
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysPermissionMapper permissionMapper;
    @Autowired
    private SysUserInfoMapper userInfoMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    public List<Map<String,String>> queryRoleList(Map map) throws Exception{
        return roleMapper.queryList(map);
    }
    public List<Map<String,String>> queryPermissionList(Map map){
        return permissionMapper.queryList(map);
    }
    public List<Map<String,String>> queryMenuPermissionListByRole(String id) throws Exception{
        return roleMapper.queryPermissionByRole(id);
    }
    @Transactional
    public void updateRolePermission(Map map) throws Exception{
        String roleId = map.get("roleId").toString();
        List<Map<String,String>> paraMapList = new ArrayList<Map<String,String>>();
        if (map.get("permissionList") != null) {
            List<String> permissionList = (List) map.get("permissionList");
            for (String permissionId : permissionList) {
                Map<String,String> rolePermission = new HashMap<String, String>();
                rolePermission.put("roleId",roleId);
                rolePermission.put("permissionId",permissionId);
                paraMapList.add(rolePermission);
            }
        }
        roleMapper.delPermissionByRole(roleId);
        roleMapper.insertRolePermission(paraMapList);
    }
    public List<Map<String,String>> queryUserList(Map map) throws Exception{
        return userInfoMapper.queryList(map);
    }

    @Transactional
    public String addUser(Map map) throws Exception{
        if (userInfoMapper.query(map) != null) {
            return "1";
        }
        String userId = IdGernerator.next();
        String salt = EncryptionUtil.getSalt();
        String password = EncryptionUtil.getPassword(map.get("username").toString(),map.get("username").toString(),salt);
        map.put("id",userId);
        map.put("state","0");
        map.put("salt",salt);
        map.put("password",password);
        map.put("userId",userId);
        userInfoMapper.insert(map);
        userRoleMapper.insert(map);
        return "0";
    }
    public Map<String,String> queryUserOne(String id) throws Exception{
        return userInfoMapper.queryOne(id);
    }
    @Transactional
    public void editUser(Map map) throws Exception{
        userInfoMapper.update(map);
        userRoleMapper.update(map);
    }
    @Transactional
    public void delUser(String userId) throws Exception{
        userInfoMapper.delete(userId);
        userRoleMapper.delete(userId);
    }
    public List<Map<String,String>> queryDepartmentList(Map map) throws Exception{
        return departmentMapper.queryList(map);
    }
    public String addDepartment(Map map)throws Exception{
        if (departmentMapper.queryOne(map) != null) {
            return "1";
        }
        map.put("id",IdGernerator.next());
        departmentMapper.insert(map);
        return "0";
    }
    public void updateDepartment(Map map) throws Exception{
        departmentMapper.update(map);
    }
    public void delDepartment(String id) throws Exception{
        departmentMapper.delete(id);
    }
    public String addRole(Map map) throws Exception{
        if (roleMapper.queryOne(map) != null) {
            return "1";
        }
        String id = IdGernerator.next();
        map.put("id",id);
        roleMapper.insert(map);
        return "0";
    }
    public void editRole(Map map) throws Exception{
        roleMapper.update(map);
    }
    @Transactional
    public void delRole(String id) throws Exception{
        roleMapper.delete(id);
        roleMapper.delPermissionByRole(id);
    }
    public List<Map<String,String>> queryMenuList(Map map){
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        map.put("username",username);
        return userInfoMapper.queryMenuList(map);
    }

}
