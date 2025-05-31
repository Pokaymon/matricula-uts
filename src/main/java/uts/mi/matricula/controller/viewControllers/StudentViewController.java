package uts.mi.matricula.controller.viewControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentViewController {

    @GetMapping("/student/materias")
    public String studentMaterias(){
	return "student/student-materias";
    }

    @GetMapping("/student/resultados")
    public String studentPensums(){
        return "student/student-resultados";
    }
}
