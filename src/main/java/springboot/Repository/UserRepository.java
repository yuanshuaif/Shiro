package springboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.Entity.SysUser;

import java.util.List;

/**
 * Created by Administrator on 2019/3/25.
 */
@Repository
public interface UserRepository extends JpaRepository<SysUser, Integer> {
    SysUser findByUsername(String username);
}
