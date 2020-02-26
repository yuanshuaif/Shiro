package springboot.Filter;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import org.springframework.cache.Cache;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.UUID;

/**
 * Created by Administrator on 2019/2/25.
 */
@Component("myfilter")
public class Myfilter extends AccessControlFilter {

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        String getPhone = servletRequest.getParameter("username");
        String getPassword = servletRequest.getParameter("password");

        /**
         * 1.生成用户身份令牌
         */
        final String phone = getPhone;
        final String password = getPassword;
        AuthenticationToken authenticationToken = new AuthenticationToken() {
            @Override
            public Object getPrincipal() {
                return phone;
            }
            @Override
            public Object getCredentials() {
                return password;
            }
        };
        boolean flag = true;
        Subject subject = null;
        try {
            // 2.获取subject
            subject = getSubject(servletRequest, servletResponse);
            // 3.通过login提交token，由DelegatingSubject委托SecurityManager处理
            /**
                 void login(AuthenticationToken token) throws AuthenticationException;
                 boolean isAuthenticated();
                 boolean isRemembered();
                 通过login登录，如果登录失败将抛出相应的AuthenticationException，
                 如果登录成功调用isAuthenticated就会返回true，即已经通过身份验证；
                 如果isRemembered返回true，表示是通过记住我功能登录的而不是调用login方法登录的。
                 isAuthenticated/isRemembered是互斥的，即如果其中一个返回true，另一个返回false。
             */
            subject.login(authenticationToken);  // 3.1转到UserRealm类
        }catch (Exception e){
            e.printStackTrace();

            // 通过流将相应的信息写出去，解决配置错误页面不生效的问题
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            if(e instanceof UnknownAccountException){
                httpResponse.getWriter().write("无效的登录用户");
            }else if(e instanceof IncorrectCredentialsException){
                httpResponse.getWriter().write("凭证匹配失败，密码错误");
            }
            flag = false;
        }
        if(flag){
            Object mappedValue = servletRequest.getAttribute("mappedValue");
            String[] roles = (String[])mappedValue;
            boolean roleCheckResult = false;
            boolean permittedCheckResult = false;
            for (int i = 0; i < roles.length; i++){
                if(roles[i].contains(":"))
                    continue;
                if (subject.hasRole(roles[i])){  // 4.1 角色授权
                    roleCheckResult = true;
                    break;
                }
            }
            for (int i = 0; i < roles.length; i++){
                if(!roles[i].contains(":"))
                    continue;
                if (subject.isPermitted(roles[i])){  // 4.2 权限授权
                    permittedCheckResult = true;
                    break;
                }
            }
            if(roleCheckResult && permittedCheckResult)
                flag = true;
        }
        // 4.subject.login(authenticationToken);认证成功之后，subject（用户信息）会放到session中进行管理
        // 也可以使用session来存储生成的token
        Session session = subject.getSession();
        // 5.认证、授权成功后，生成accessToken，后续登录可通过accessToken进行登录
        if(flag){
            tokenCache(phone);
        }
        return flag;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        request.setAttribute("mappedValue", mappedValue);
        return this.onAccessDenied(request, response);
    }

    /**
     * 缓存
     * @param phone
     */
    public void tokenCache(String phone){
        Cache cache = redisCacheManager.getCache("accessToken");
        // 互踢
        cache.evict(phone);
        // "akjsdhqiuwqne1i2uykjashd1"
        String value = UUID.randomUUID().toString();
        cache.put(phone, value);
    }
}
