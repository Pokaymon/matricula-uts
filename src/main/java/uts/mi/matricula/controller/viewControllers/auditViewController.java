package uts.mi.matricula.controller.viewControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class auditViewController {

    @GetMapping("/audit")
    public String pensums(){
	return "audit/audit_pensum";
    }

}
