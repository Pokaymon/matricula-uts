package uts.mi.matricula;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

  @GetMapping("/")
  public String home(){
    return "home";
  }

  @GetMapping("/login")
  public String login(){
    return "login";
  }

  @GetMapping("/admin")
  public String admin(){
    return "admin";
  }

  @GetMapping("/student")
  public String student(){
    return "student";
  }

  @GetMapping("/coordinator")
  public String coordinator(){
    return "coordinator";
  }

  @GetMapping("/teacher")
  public String teacher(){
    return "teacher";
  }

  @GetMapping("/audit")
  public String audit(){
    return "audit";
  }

  @GetMapping("/unauthorized")
  public String unauthorized(){
    return "unauthorized";
  }
}
