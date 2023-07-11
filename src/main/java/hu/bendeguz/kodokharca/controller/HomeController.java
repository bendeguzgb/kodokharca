package hu.bendeguz.kodokharca.controller;

import hu.bendeguz.kodokharca.model.GameNumber;
import hu.bendeguz.kodokharca.service.CombinationGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private static final String HOME_SCREEN = "home";
    private static final int ELEMENTS_IN_ARRAY_5_NUMBERS = 5;
    private static final int ELEMENTS_IN_ARRAY_4_NUMBERS = 4;
    private List<List<GameNumber>> combinations5Numbers = CombinationGenerator.generateAllCombinations(ELEMENTS_IN_ARRAY_5_NUMBERS);
    private List<List<GameNumber>> combinations4Numbers = CombinationGenerator.generateAllCombinations(ELEMENTS_IN_ARRAY_4_NUMBERS);


    @GetMapping(value = {"/", "/home"})
    public String showHome(@RequestParam(required = false, defaultValue = "5") Integer elementsInArray, Model model) {
        model.addAttribute("combinations", CombinationGenerator.generateAllCombinations(elementsInArray));
        model.addAttribute("elementsInArray", elementsInArray);
        model.addAttribute("headerIterable", new boolean[elementsInArray]);

        return HOME_SCREEN;
    }
}
