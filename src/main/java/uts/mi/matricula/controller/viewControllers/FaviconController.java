package uts.mi.matricula.controller.viewControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FaviconController {

    @RequestMapping("favicon.ico")
    public String favicon() {
        return "redirect:/img/favicon.png";
    }
}
