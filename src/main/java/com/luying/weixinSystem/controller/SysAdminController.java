package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSON;
import com.luying.weixinSystem.service.SysAdminService;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sysAdmin")
public class SysAdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysAdminController.class);

    @Autowired
    private SysAdminService adminService;

    @GetMapping("/getMenuList")
    public String getMenuPermissionList(Map map){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryMenuList(map));
        } catch (Exception e) {
            LOGGER.info("getMenuList_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }

    @GetMapping("/getRoleList")
    public String queryRoleList(Map map){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryRoleList(map));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("getRoleList_exception:" + e);
            result = "-1";
        }
        return  result;
    }
    @GetMapping("/getMenuPermissionList")
    public String queryMenuPermissionList(Map map){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryPermissionList(map));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("getMenuPermissionList_exception:" + e);
            result = "-1";
        }
        return  result;
    }
    @GetMapping("/getMenuPermissionListByRole/{id}")
    public String queryMenuPermissionListByRole(@PathVariable String id){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryMenuPermissionListByRole(id));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("queryMenuPermissionListByRole_exception:" + e);
            result = "-1";
        }
        return  result;
    }
    @PostMapping("/updateRolePermission")
    public String updateRolePermission(@RequestBody Map map){
        String result = "0";
        try {
            adminService.updateRolePermission(map);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("updateRolePermission_exception:" + e);
            result = "-1";
        }
        return  result;
    }

    @GetMapping("/getUserList")
    public String getUserList(Map map){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryUserList(map));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("getUserList_exception:" + e);
            result = "-1";
        }
        return result;
    }
    @GetMapping("/getRoleListForSelect")
    public String getRoleListForSelect(Map map){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryRoleList(map));
        } catch (Exception e) {
            LOGGER.info("getRoleList_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @PostMapping("/addUser")
    public String addUser(@RequestBody Map map){
        String result = "0";
        try {
            result = adminService.addUser(map);
        } catch (Exception e) {
            LOGGER.info("addUser_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @GetMapping("/getUserOne/{id}")
    public String getUserOne(@PathVariable String id){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryUserOne(id));
        } catch (Exception e) {
            LOGGER.info("getUserOne_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @PostMapping("/editUser")
    public String editUser(@RequestBody Map map){
        String result = "0";
        try {
            adminService.editUser(map);
        } catch (Exception e) {
            LOGGER.info("editUser_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @GetMapping("/delUser/{id}")
    public String delUser(@PathVariable String id){
        String result = "0";
        try {
            adminService.delUser(id);
        } catch (Exception e) {
            LOGGER.info("delUser_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @GetMapping("/getDepartmentList")
    public String getDepartmentList(Map map){
        String result = "";
        try {
            result = JSON.toJSONString(adminService.queryDepartmentList(map));
        } catch (Exception e) {
            LOGGER.info("getDepartmentList_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @PostMapping("/addDepartment")
    public String addDepartment(@RequestBody Map map){
        String result = "0";
        try {
            result = adminService.addDepartment(map);
        } catch (Exception e) {
            LOGGER.info("addDepartment_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @PostMapping("/editDepartment")
    public String editDepartment(@RequestBody Map map){
        String result = "0";
        try {
            adminService.updateDepartment(map);
        } catch (Exception e) {
            LOGGER.info("editDepartment_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @GetMapping("/delDepartment/{id}")
    public String delDepartment(@PathVariable String id){
        String result = "0";
        try {
            adminService.delDepartment(id);
        } catch (Exception e) {
            LOGGER.info("delDepartment_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @PostMapping("/addRole")
    public String addRole(@RequestBody Map map){
        String result = "0";
        try {
            result = adminService.addRole(map);
        } catch (Exception e) {
            LOGGER.info("addRole_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @PostMapping("/editRole")
    public String editRole(@RequestBody Map map){
        String result = "0";
        try {
            adminService.editRole(map);
        } catch (Exception e) {
            LOGGER.info("editRole_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @GetMapping("/delRole/{id}")
    public String delRole(@PathVariable String id){
        String result = "0";
        try {
            adminService.delRole(id);
        } catch (Exception e) {
            LOGGER.info("delRole_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }
    @GetMapping("/getCurUserInfo")
    public String getCurUserInfo(){
        String result = "0";
        try {
            result = JSON.toJSONString(SecurityUtils.getSubject().getSession().getAttribute("USER"));
        } catch (Exception e) {
            LOGGER.info("getCurUserInfo_exception:" + e);
            e.printStackTrace();
            result = "-1";
        }
        return result;
    }

}
