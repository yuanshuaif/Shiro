package springboot.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.Entity.SysUser;
import springboot.Repository.UserRepository;
import springboot.Service.ISysUserService;

import java.util.List;

/**
 * Created by Administrator on 2019/3/25.
 */
@Service
public class SysUserServiceImpl implements ISysUserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public SysUser findByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
