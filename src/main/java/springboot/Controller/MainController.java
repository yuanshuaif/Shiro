package springboot.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2019/3/25.
 */
@Controller
public class MainController {

    @RequestMapping("/login")
    String login(){
        return "login";
    }

    @RequestMapping("/alogin")
    @ResponseBody  String alogin(){
        return "alogin";
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
