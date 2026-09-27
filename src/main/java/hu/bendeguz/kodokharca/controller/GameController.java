package hu.bendeguz.kodokharca.controller;

import hu.bendeguz.kodokharca.model.Color;
import hu.bendeguz.kodokharca.model.GameDetails;
import hu.bendeguz.kodokharca.model.Question;
import hu.bendeguz.kodokharca.service.GameManager;
import hu.bendeguz.kodokharca.service.question.GameCodeGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Validated
@Controller
@RequestMapping("/jatek")
public class GameController {

    private static final String GAME_SCREEN = "game";
    private static final String OPPONENT_COUNT = "ellenfelekSzama";
    private static final String CODE = "kod";
    private static final String QUESTIONS = "feltettKerdesek";
    private static final String GAME_DETAILS = "gameDetails";
    
    private final GameManager gameManager;
    
    
    @GetMapping
    public String game(
        @RequestParam(name = OPPONENT_COUNT, required = false) @Range(min = 1, max = 3) Integer opponentCount,
        @RequestParam(name = CODE, defaultValue = "") @Pattern(regexp = "[0-9a-zA-Z]{0,10}") String code,
        @RequestParam(name = QUESTIONS, defaultValue = "") List<String> playersAskedQuestions,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (opponentCount == null) {
            return "redirect:/";
        }
        
        if (!StringUtils.hasLength(code)) {
            code = GameCodeGenerator.createCode();
            
            redirectAttributes
                .addAttribute(OPPONENT_COUNT, opponentCount)
                .addAttribute(CODE, code)
                .addAttribute(QUESTIONS, playersAskedQuestions);
            return "redirect:/jatek";
        }
        
        List<Question> playersQuestions = Question.ofNames(playersAskedQuestions);
        GameDetails gameDetails = gameManager.manageGame(opponentCount, code, playersQuestions);
        
        if (gameDetails.isGameOver()) {
            // todo: redirect to final page
//            redirectAttributes.addAttribute("gameDetails", gameDetails);
//            return "redirect:/game";
        }
        
        Map<String, Object> templateData = createTemplateData(opponentCount, code, playersAskedQuestions, gameDetails);
        model.addAllAttributes(templateData);
        
        return GAME_SCREEN;
    }
    
    private Map<String, Object> createTemplateData(
        int opponentCount,
        String code,
        List<String> playersAskedQuestions,
        GameDetails gameDetails
    ) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put(OPPONENT_COUNT, opponentCount);
        templateData.put(CODE, code);
        templateData.put(QUESTIONS, playersAskedQuestions);
        templateData.put(GAME_DETAILS, gameDetails);
        
        
        ArrayList<String> letters = new ArrayList<>(List.of("A", "B", "C", "D"));
        int playerCount = opponentCount + 1;
        
        if (playerCount == 2 || playerCount == 3) {
            letters.add("E");
        }
        templateData.put("letters", letters);
        
        
        templateData.put("numbers", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        templateData.put("colors", List.of(Color.WHITE.name(), Color.BLACK.name(), Color.GREEN.name()));
        templateData.put("searchText", "dummy search text"); // js insert
        
        
        List<String> availableQuestions = gameDetails.getAvailableQuestions();
        int availableQuestionCount = availableQuestions.size();
        
        templateData.put("questionsRow1", availableQuestions.subList(0, Math.min(3, availableQuestionCount)));
        templateData.put("questionsRow2", List.of());
        
        if (availableQuestionCount > 3) {
            templateData.put("questionsRow2", availableQuestions.subList(3, availableQuestionCount));
        }
        
        return templateData;
    }
}
