1.建实体类、表、repository、service等
2.web.xml配置(springboot 可以不配置)
    DelegatingFilterProxy作用是自动到spring容器查找名字为shiroFilter（filter-name）
    的bean并把所有Filter的操作委托给它。
    <filter>
        <!--代理的filter的名称-->
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
     </filter>
     <filter-mapping>
         <filter-name>shiroFilter</filter-name>
         <url-pattern>/*</url-pattern>
     </filter-mapping>
2.spring.xml配置（配置类 配置）
    缓存管理器  使用redisCacheManager实现缓存管理器
    凭证匹配器（自定义的密码校验） 具体实体类
    自定义的Realm(使用缓存管理器、凭证匹配器) 具体实体类
    subjectFactory工厂  具体实体类  // 将subject绑定到SecurityManager上
    session管理器
    配置SecurityManager（Realm、缓存管理器、session管理器、subjectFactory）

    配置MethodInvokingFactoryBean（将SecurityManager与Subject绑定到一起）

    配置ShiroFilterFactoryBean（Shiro的Web过滤器）


