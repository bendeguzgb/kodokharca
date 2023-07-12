package hu.bendeguz.kodokharca.controller;

import hu.bendeguz.kodokharca.model.GameNumber;
import hu.bendeguz.kodokharca.service.CombinationFilterHandler;
import hu.bendeguz.kodokharca.service.CombinationGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private static final String HOME_SCREEN = "home";
    private static final int ELEMENTS_IN_ARRAY_5_NUMBERS = 5;
    private static final int ELEMENTS_IN_ARRAY_4_NUMBERS = 4;
    private List<List<GameNumber>> combinations5Numbers = CombinationGenerator.generateAllCombinations(ELEMENTS_IN_ARRAY_5_NUMBERS);
    private List<List<GameNumber>> combinations4Numbers = CombinationGenerator.generateAllCombinations(ELEMENTS_IN_ARRAY_4_NUMBERS);


    @GetMapping(value = {"/", "/home"})
    public String showHome(@RequestParam(required = false, defaultValue = "5") Integer elementsInArray,
        @RequestParam(required = false, defaultValue = "") List<String> filter, Model model) {

        log.debug("filter = '{}'", filter);
        log.debug("elementsInArray = '{}' ", elementsInArray);

        List<List<GameNumber>> combinations;

        switch (elementsInArray) {
            case 5: {
                combinations = combinations5Numbers;
                break;
            }
            case 4: {
                combinations = combinations4Numbers;
                break;
            }
            default: {
                combinations = CombinationGenerator.generateAllCombinations(elementsInArray);
            }
        }

        combinations = (filter.size() > 0) ? CombinationFilterHandler.handleFiltering(combinations, filter) : combinations;
        log.debug("Returning {} combinations!", combinations.size());

        model.addAttribute("combinations", combinations);
        model.addAttribute("elementsInArray", elementsInArray);
        model.addAttribute("headerIterable", new boolean[elementsInArray]);

        return HOME_SCREEN;
    }
}
