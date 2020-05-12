package springboot.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;

/**
 * Created by Administrator on 2019/3/25.
 */
@Controller
public class MainController {

    @Autowired
    private RedisCacheManager redisCacheManager;

    @RequestMapping("/login")
    String login(){
        return "login";
    }

    @RequestMapping("/alogin")
    @ResponseBody  String alogin(ServletRequest servletRequest){
        String phone = servletRequest.getParameter("username");

        String accessToken = "";
        Cache cache = redisCacheManager.getCache("accessToken");
        // #### "accessToken:" 缓存管理器的前缀 保存或获取时 无需携带
        Cache.ValueWrapper wrapper = cache.get(phone);
        if(wrapper != null){
            accessToken = (String) wrapper.get();
        }
        return accessToken;
    }

    @RequestMapping("/index")
    String index(){
        return "index";
    }

    @RequestMapping("/403")
    String error(){
        return "403";
    }
}
