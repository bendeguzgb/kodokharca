package hu.bendeguz.kodokharca.controller;

import hu.bendeguz.kodokharca.model.GameNumber;
import hu.bendeguz.kodokharca.service.CombinationManager;
import hu.bendeguz.kodokharca.service.NumberCombinationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private static final String HOME_SCREEN = "home";
    private static final int ELEMENT_COUNT = 20;
    private static final int ELEMENTS_IN_ARRAY = 5;


    @GetMapping(value = {"/", "/home"})
    public String showHome(Model model) {
        List<List<GameNumber>> combinations = CombinationManager.generateAllCombinations(ELEMENT_COUNT, ELEMENTS_IN_ARRAY);
        model.addAttribute("values", combinations);

        return HOME_SCREEN;
    }
}
