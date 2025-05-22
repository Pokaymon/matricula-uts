package uts.mi.matricula.controller.viewControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfesorViewController {

    @GetMapping("/grupos")
    public String grupos(){
	return "teacher/grupos";
    }

}
