package com.luying.weixinSystem.config;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 鉴权拦截器
 */
public class UrlPermissionFilter extends PermissionsAuthorizationFilter {
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        return super.isAccessAllowed(request, response, buildPermissions(request));
    }
    public String[] buildPermissions(ServletRequest request){
        String[] permissions = new String[1];
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getServletPath();
        permissions[0] = path;
        return  permissions;
    }
}
