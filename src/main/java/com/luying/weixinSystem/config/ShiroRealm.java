package com.luying.weixinSystem.config;

import com.luying.weixinSystem.service.SysAdminService;
import com.luying.weixinSystem.service.SysUserInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiroRealm extends AuthorizingRealm {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroRealm.class);
    private static final String AUTHORIZATIONINFO = "authorizationInfo";

    @Autowired
    SysUserInfoService sysUserInfoService;
    @Autowired
    private SysAdminService adminService;

    /**
     * 权限配置
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
        Session session = SecurityUtils.getSubject().getSession();
        SimpleAuthorizationInfo authorizationInfo = (SimpleAuthorizationInfo) session.getAttribute(AUTHORIZATIONINFO);
        if (authorizationInfo == null) {
            authorizationInfo = new SimpleAuthorizationInfo();
            List<Map<String,String>> permissionList = adminService.queryMenuList(new HashMap());
            for (Map<String,String> map : permissionList) {
                authorizationInfo.addStringPermission(map.get("url"));
            }
            session.setAttribute(AUTHORIZATIONINFO,authorizationInfo);
        }
        return authorizationInfo;
    }

    /**
     * 身份验证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();
        Map<String,String> map = new HashMap<>();
        map.put("username",userName);

        Map<String,String> userInfo = sysUserInfoService.query(map);
        if (userInfo == null) {
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userName,
                userInfo.get("password"),
                ByteSource.Util.bytes(userInfo.get("username") + userInfo.get("salt")),//salt = username + salt
                getName());
        return authenticationInfo;
    }
}
