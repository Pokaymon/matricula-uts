package uts.mi.matricula.controller.viewControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class pensumViewController {

    @RequestMapping("/users/pensum")
    public String favicon() {
        return "teacher/estudiantes-pensum";
    }
}
