package hu.bendeguz.kodokharca.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private static final String HOME_SCREEN = "home";
    
    @GetMapping
    public String showHomeScreen() {
        return HOME_SCREEN;
    }
}
