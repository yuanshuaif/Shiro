package springboot.Service;

import springboot.Entity.SysUser;

import java.util.List;

/**
 * Created by Administrator on 2019/3/25.
 */
public interface ISysUserService {

    SysUser findByUsername(String username);
}
