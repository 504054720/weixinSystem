package com.luying.weixinSystem.config;

import com.luying.weixinSystem.service.SysAdminService;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.*;

@Configuration
public class ShiroConfig {
    @Autowired
    private SysAdminService adminService;
    private static final Map<String,String> filterChainDefinationMap = new LinkedHashMap<>();

    /**
     * 设置不拦截的请求url
     */
    static {
        filterChainDefinationMap.put("/static/**","anon");
        filterChainDefinationMap.put("/doLogin","anon");
        filterChainDefinationMap.put("/templates/mobile/**","anon");
        filterChainDefinationMap.put("/signRule/**","anon");
        filterChainDefinationMap.put("/signRecord/**","anon");
        filterChainDefinationMap.put("/volidateWx","anon");
        filterChainDefinationMap.put("/payInfo/**","anon");
        filterChainDefinationMap.put("/employee/**","anon");
        filterChainDefinationMap.put("/fuwuhao/**","anon");
        filterChainDefinationMap.put("/wxOpen/**","anon");
        filterChainDefinationMap.put("/MP_verify_tQ0IRvSmeLrqSKt1.txt","anon");
    }


    /**
     * shiro过滤器
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,UrlPermissionFilter urlPermissionFilter){
        InitShiroFilterFactoryBean shiroFilterFactoryBean = new InitShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        filterChainDefinationMap.put("/logout","logout");
      //  filterChainDefinationMap.putAll(getPermissionList());
        filterChainDefinationMap.put("/**","authc");
        shiroFilterFactoryBean.setLoginUrl("/login");
        //shiroFilterFactoryBean.setSuccessUrl("/index");
        //shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("urlPerms",urlPermissionFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinationMap);

        return  shiroFilterFactoryBean;
    }

    /**
     * token校验器
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }

    /**
     * realm
     * @param hashedCredentialsMatcher
     * @return
     */
    @Bean
    public ShiroRealm shiroRealm(HashedCredentialsMatcher hashedCredentialsMatcher){
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        shiroRealm.setCachingEnabled(false);
        shiroRealm.setAuthenticationCachingEnabled(false);
        return shiroRealm;
    }

    /**
     * 安全管理器
     * @param shiroRealm
     * @param sessionManager
     * @return
     */
    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm,SessionManager sessionManager){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    /**
     * session配置
     * @return
     */
    @Bean
    public SessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        SimpleCookie cookie = new SimpleCookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
        cookie.setMaxAge(60*60*60);
        //cookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }
    //@Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        SimpleCookie cookie = new SimpleCookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME);
        //cookie.setHttpOnly(true);
        cookie.setMaxAge(60*60*60);
        rememberMeManager.setCookie(cookie);
        return rememberMeManager;
    }

    /**
     * 打开shiro aop注解
     * @param securityManager
     * @return
     */
    //@Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    @Bean
    public UrlPermissionFilter urlPermissionFilter(){
        return new UrlPermissionFilter();
    }

    public Map<String,String> getPermissionList(){
        List<Map<String,String>> permissionList = adminService.queryPermissionList(new HashMap());
        Map<String,String> permissionMap = new HashMap<>();
        for (Map<String,String> map : permissionList) {
            permissionMap.put(map.get("url"),"perms["+map.get("url")+"]");
        }
        return permissionMap;
    }
}
