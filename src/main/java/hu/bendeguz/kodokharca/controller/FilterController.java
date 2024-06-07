package hu.bendeguz.kodokharca.controller;

import hu.bendeguz.kodokharca.model.GameNumber;
import hu.bendeguz.kodokharca.service.CombinationFilterHandler;
import hu.bendeguz.kodokharca.service.CombinationGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/szures")
public class FilterController {
    
    private static final String FILTER_SCREEN = "filter";
    
    
    @GetMapping
    public String showHome(
            @RequestParam(name = "dbszam", required = false, defaultValue = "5") Integer elementsInArray,
            @RequestParam(name = "szuresek", required = false) List<String> filters,
            Model model) {
        if (filters == null) {
            return String.format("redirect:/szures?dbszam=%d&szuresek=", elementsInArray);
        }
        
        log.debug("filters = '{}'", filters);
        log.debug("elementsInArray = '{}' ", elementsInArray);
        
        List<List<GameNumber>> combinations = CombinationGenerator.generateAllCombinations(elementsInArray);
        combinations = CombinationFilterHandler.handleFiltering(combinations, filters);
        
        log.debug("Returning {} combinations!", combinations.size());
        
        model.addAttribute("combinations", combinations);
        model.addAttribute("elementsInArray", elementsInArray);
        model.addAttribute("headerIterable", new boolean[elementsInArray]);
        
        return FILTER_SCREEN;
    }
}
