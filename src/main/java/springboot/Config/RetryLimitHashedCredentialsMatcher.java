package springboot.Config;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springboot.Entity.SysUser;
import springboot.Service.ISysUserService;

/**
 * Created by Administrator on 2019/3/25.
 * 自定义的凭证比较器
 */
@Component
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    // 自定义密码加密实现
     @Autowired
    private ISysUserService sysUserService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, final AuthenticationInfo info) {
        String inputUserName = (String) token.getPrincipal();
        String inputPassword = (String) token.getCredentials();
//        ByteSource salt = ByteSource.Util.bytes(inputUserName);
        String password = new Md5Hash(inputPassword, inputUserName, 2).toString();
        String secretPassword = (String) info.getCredentials();
        boolean flag = false;
        if(secretPassword.equals(password)){
            flag = true;
        }else{
            throw new IncorrectCredentialsException();
        }
        return flag;
    }
}
