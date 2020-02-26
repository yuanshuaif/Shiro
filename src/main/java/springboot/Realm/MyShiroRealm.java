package springboot.Realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springboot.Entity.SysPermission;
import springboot.Entity.SysRole;
import springboot.Entity.SysUser;
import springboot.Service.ISysUserService;

/**
 * Created by Administrator on 2019/3/25.
 */
@Component
public class MyShiroRealm extends AuthorizingRealm{

    @Autowired
    private ISysUserService sysUserService;

    /**
     * ### Realm实现类和token类是绑定关系
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof AuthenticationToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        SysUser userInfo  = (SysUser)principals.getPrimaryPrincipal();
        for(SysRole role:userInfo.getRoleList()){
            authorizationInfo.addRole(role.getRole());// 添加角色信息
            for(SysPermission p:role.getPermissions()){
                authorizationInfo.addStringPermission(p.getPermission()); // 添加权限信息
            }
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("认证配置-->MyShiroRealm.doGetAuthenticationInfo()");
        //1.从token中获取用户名、密码
        String username = (String)token.getPrincipal();
        System.out.println(token.getCredentials());
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        SysUser sysUser = sysUserService.findByUsername(username);
        System.out.println("----->>userInfo="+sysUser);
        if(sysUser == null){
            throw new UnknownAccountException();
        }
        // #### 默认用盐加密密码，与此处的密文密码进行比较
        // #### 第一个参数和第四个参数  一个realm有一个身份认证的集合
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                sysUser, //用户实体类
                sysUser.getPassword(), //密码（密文）
                //  String password = new Md5Hash(originalPassword, userName, 2).toString(); // password数据库存储的密码为密文
                ByteSource.Util.bytes(username),// salt (md5 密码+盐 进行2次计算得到密文)
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
