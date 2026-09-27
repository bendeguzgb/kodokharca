package hu.bendeguz.kodokharca.model;

import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
@Setter(AccessLevel.NONE)
public class Player {
    
    private int position;
    private List<GameNumber> gameNumbers;
    
    // questionsAskedAndAnswers:
    //      key: player.position
    //      value: Question asked about player with this position and the Answer
    private Map<Integer, Map<Question, String>> questionsAskedAndAnswers;
    private boolean isHuman;
    
    public String chooseQuestion(List<String> preparedAvailableQuestions, List<String> allQuestions) {
        if (isHuman) {
            throw new UnsupportedOperationException("This function should not be called on human players!");
        }
        return preparedAvailableQuestions.get(0);
    }
}
