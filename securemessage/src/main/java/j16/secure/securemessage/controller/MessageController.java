package j16.secure.securemessage.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MessageController {
    
    @RequestMapping("/")
    public String home()   {
        return "index";
    }
    
    
}
