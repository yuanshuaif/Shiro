package springboot.Entity;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2019/3/25.
 */
@Entity
public class SysUser {
    @Id
    @GeneratedValue
    private Integer uid;
    @Column(unique =true)
    private String username;//帐号
    private String name;//名称（昵称或者真实姓名，不同系统不同定义）
    private String password; //密码;
    private String salt;//加密密码的盐
    private byte state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.
    @ManyToMany(fetch= FetchType.EAGER)//立即从数据库中进行加载数据;
    @JoinTable(name = "SysUserRole", joinColumns = { @JoinColumn(name = "uid") }, inverseJoinColumns ={@JoinColumn(name = "roleId") })
    private List<SysRole> roleList;// 一个用户具有多个角色

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public List<SysRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SysRole> roleList) {
        this.roleList = roleList;
    }
    public byte[] getCredentialsSalt() {
        if (!StringUtils.isEmpty(this.username)) {
            byte[] byte1 = this.username.getBytes();
            byte[] byte2 = randomSalt(10);
            byte[] byte3 = new byte[byte1.length + byte2.length];

            System.arraycopy(byte1, 0, byte3, 0, byte1.length);
            System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);

            return byte3;
        }
        return null;
    }

    public static byte[] randomSalt(int size) {
        Random random = new Random();
        byte[] salt = new byte[size];
        random.nextBytes(salt);
        return salt;
    }
    @Override
    public String toString() {
        return "SysUser{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", state=" + state +
//                ", roleList=" + roleList +
                '}';
    }
}
