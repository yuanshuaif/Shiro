package springboot.Config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springboot.Filter.Myfilter;
import springboot.Filter.Tokenfilter;
import springboot.Realm.MyShiroRealm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/25.
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private MyShiroRealm myShiroRealm;
    @Autowired
    private Myfilter myfilter;
    @Autowired
    private Tokenfilter tokenfilter;
 /*  @Autowired
    private SubjectFactory subjectFactory;*/
    @Autowired
    private RetryLimitHashedCredentialsMatcher credentialsMatcher;

    /**
     * 2.Shiro的Web过滤器、Shiro主过滤器
     * ShiroFilterFactoryBean的配置
     * 哪些过滤器受它的管理
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        System.out.println("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 1.securityManager的配置
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 2.filters的配置
        Map filters = new LinkedHashMap();
        filters.put("myfilter", myfilter);  // 认证、权限的主入口
        filters.put("tokenfilter", tokenfilter);
        shiroFilterFactoryBean.setFilters(filters);
        // 3.filterChainDefinition 过滤链的配置
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 部分过滤器可指定参数，如 perms，roles
        filterChainDefinitionMap.put("/login", "anon");// 静态页面
        filterChainDefinitionMap.put("/alogin", "myfilter[admin,userInfo:view]");// 登录请求
//        filterChainDefinitionMap.put("/alogin", "authc");// 登录请求（未生效）
        filterChainDefinitionMap.put("/index", "tokenfilter");// 站内地址
        shiroFilterFactoryBean.setLoginUrl("/login");

//        shiroFilterFactoryBean.setUnauthorizedUrl("/403");// 以流的形式输出，不在采用这种方式
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 0.SecurityManager的配置
     * @return
     */
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        // 2.Subject工厂 将subject绑定到SecurityManager上（另一种方式）（再次验证时不生效了）
//        securityManager.setSubjectFactory(subjectFactory);
        // 1.凭证匹配器
        credentialsMatcher.setHashAlgorithmName("md5");
        credentialsMatcher.setHashIterations(2);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        myShiroRealm.setCredentialsMatcher(credentialsMatcher);
        myShiroRealm.setAuthenticationCachingEnabled(true);
        myShiroRealm.setAuthorizationCachingEnabled(true);
        // 1.realm
        securityManager.setRealm(myShiroRealm);
        // 2.设置回话管理器
        DefaultSessionManager defaultSessionManager = new DefaultSessionManager();
        defaultSessionManager.setSessionValidationSchedulerEnabled(false); // ### 不设置报错
        securityManager.setSessionManager(defaultSessionManager);
        return securityManager;
    }

    /**
     * 1.相当于调用SecurityUtils.setSecurityManager(securityManager)
     * 将SecurityManager与Subject绑定到一起
     * @return
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(){
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        methodInvokingFactoryBean.setArguments(securityManager());
        return methodInvokingFactoryBean;
    }
}
