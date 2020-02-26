package springboot;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * Created by Administrator on 2019/3/26.
 */
public class Test {
    /**
     * yong md5 jiami
     * @param args
     */
    public static void main(String[] args) {
        String hashAlgorithmName = "MD5";
        // d0d400f365190fd84551b6486ec4c0e2
        String credentials = "d3c59d25033dbf980d29554025c23a75";
        int hashIterations = 2;
        ByteSource credentialsSalt = ByteSource.Util.bytes("admin");
        Object obj = new SimpleHash(hashAlgorithmName, credentials, credentialsSalt, hashIterations);
        System.out.println(obj);
    }
}
