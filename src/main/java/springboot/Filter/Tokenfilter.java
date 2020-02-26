package springboot.Filter;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用token进行登录
 * Created by Administrator on 2019/2/31.
 */
@Component("tokenfilter")
public class Tokenfilter extends AccessControlFilter {

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        String phone = servletRequest.getParameter("username");
        String token = servletRequest.getParameter("token");

        boolean flag = false;
        Cache cache = redisCacheManager.getCache("accessToken");
        // #### "accessToken:" 缓存管理器的前缀 保存或获取时 无需携带
        Cache.ValueWrapper wrapper = cache.get(phone);
        if(wrapper != null){
            String accessToken = (String) wrapper.get();
            if(token != null && token.equals(accessToken)){
                flag = true;
            }
        }
        if(!flag){
            // 通过流将相应的信息写出去，前端根据code去展示登录页面
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            Map<String, String> result = new HashMap<>();
            result.put("code", "1001");
            result.put("msg", "用户登录失效请重新登录");
            httpResponse.getWriter().write(result.toString());
        }
        return flag;
    }

}
