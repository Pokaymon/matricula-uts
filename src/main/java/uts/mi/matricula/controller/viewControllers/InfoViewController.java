package uts.mi.matricula.controller.viewControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/info")
public class InfoViewController {

  @GetMapping("/admin")
  public String adminInfo(){
    return "info/admin-info";
  }

  @GetMapping("/coordinator")
  public String coordinatorInfo(){
    return "info/coordinador-info";
  }

  @GetMapping("/teacher")
  public String teacherInfo(){
    return "info/profesor-info";
  }

  @GetMapping("/student")
  public String studentInfo(){
    return "info/student-info";
  }

  @GetMapping("/audit")
  public String auditInfo(){
    return "info/audit-info";
  }
}
