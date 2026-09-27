package hu.bendeguz.kodokharca.service.question;

import static hu.bendeguz.kodokharca.service.Utils.sumOfSubList;

import hu.bendeguz.kodokharca.model.Color;
import hu.bendeguz.kodokharca.model.GameNumber;
import hu.bendeguz.kodokharca.model.QuestionAnswerDTO;
import hu.bendeguz.kodokharca.model.QuestionsAndFilteringTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionHandler {
    
    private static final String QUESTION_NAME_VALUE_SEPARATOR = "-";
    
    /**
     * @param question  Patter eg: guess-01566-hkzkh / sum-15 / numberLocation-6
     */
    public static QuestionAnswerDTO askQuestion(String question, List<GameNumber> combination) {
        log.info("New question asked: '{}'", question);
        
        String[] questionSplit = question.split(QUESTION_NAME_VALUE_SEPARATOR);
        String questionName = questionSplit[0];
        
        Predicate<GameNumber> predicate = null;
        BiPredicate<GameNumber, GameNumber> biPredicate = null;
        
        switch (QuestionsAndFilteringTypes.ofName(questionName)) {
            case GUESS: {
                // guess-01566-hkzkh
                String[] numberGuesses = questionSplit[1].split("");
                String[] colorGuesses = questionSplit[2].split("");
                
                for (int i = 0; i < combination.size(); i++) {
                    int number = Integer.parseInt(numberGuesses[i]);
                    Color color = Color.fromChar(colorGuesses[i]);
                    
                    GameNumber guess = new GameNumber(number, color);
                    if (!combination.get(i).equals(guess)) {
                        return QuestionAnswerDTO.ofIsTrue(false);
                    }
                }
                
                return QuestionAnswerDTO.ofIsTrue(true);
            }
            case SUM: {
                predicate = gameNumber -> true;
            }
            case BLACK_SUM: {
                if (predicate == null) {
                    predicate = GameNumber::isBlackNumber;
                }
            }
            case WHITE_SUM: {
                if (predicate == null) {
                    predicate = GameNumber::isWhiteNumber;
                }
                
                int sum = combination.stream()
                    .filter(predicate)
                    .mapToInt(GameNumber::getValue)
                    .sum();
                return QuestionAnswerDTO.ofSum(sum);
            }
            case LEFT_SUM: {
                int sum = sumOfSubList(combination, 0, 3);
                return QuestionAnswerDTO.ofSum(sum);
            }
            case RIGHT_SUM: {
                int sum = sumOfSubList(combination, combination.size() - 3, combination.size());
                return QuestionAnswerDTO.ofSum(sum);
            }
            case MIDDLE_SUM: {
                int sum;
                int middle = combination.size() / 2;
                
                if (combination.size() % 2 == 0) {
                    sum = sumOfSubList(combination, middle - 1, middle + 1); // B+C
                } else {
                    sum = sumOfSubList(combination, middle - 1, middle + 2); // B+C+D
                }
                
                return QuestionAnswerDTO.ofSum(sum);
            }
            case BLACK_COUNT: {
                predicate = GameNumber::isBlackNumber;
            }
            case WHITE_COUNT: {
                if (predicate == null) {
                    predicate = GameNumber::isWhiteNumber;
                }
            }
            case EVEN: {
                if (predicate == null) {
                    predicate = gameNumber -> gameNumber.getValue() % 2 == 0;
                }
            }
            case ODD: {
                int count;
                if (predicate == null) {
                    predicate = gameNumber -> gameNumber.getValue() % 2 == 1;
                }
                
                count = (int) combination.stream()
                    .filter(predicate)
                    .count();
                return QuestionAnswerDTO.ofCount(count);
            }
            case HIGHEST_LOWEST: {
                int diff = combination.get(combination.size() - 1).getValue() - combination.get(0).getValue();
                return QuestionAnswerDTO.ofDiff(diff);
            }
            case IS_C_BIGGER: {
                boolean isBigger = combination.get(2).getValue() > 4;
                return QuestionAnswerDTO.ofIsTrue(isBigger);
            }
            case NUMBER_LOCATION: {
                // Expected format: numberLocation-6
                int number = Integer.parseInt(questionSplit[1]);
                List<Integer> indexes = new ArrayList<>();
                
                for (int i = 0; i < combination.size(); i++) {
                    if (combination.get(i).getValue() == number) {
                        indexes.add(i);
                    }
                }
                return QuestionAnswerDTO.ofIndexes(indexes);
            }
            case CONSECUTIVE_NEIGHBOUR: {
                biPredicate = (gameNumber1, gameNumber2) -> gameNumber1.getValue() + 1 == gameNumber2.getValue();
            }
            case SAME_COLOR_NEIGHBOUR: {
                if (biPredicate == null) {
                    biPredicate = (gameNumber1, gameNumber2) -> gameNumber1.getColor() == gameNumber2.getColor();
                }
            }
            case SAME_NUMBER_PAIRS: {
                if (biPredicate == null) {
                    biPredicate = (gameNumber1, gameNumber2) -> gameNumber1.getValue() == gameNumber2.getValue();
                }
                List<Integer> indexes = new ArrayList<>();
                
                for (int i = 0; i < combination.size() - 1; i++) {
                    GameNumber gameNumber1 = combination.get(i);
                    GameNumber gameNumber2 = combination.get(i + 1);
                    
                    if (biPredicate.test(gameNumber1, gameNumber2)) {
                        indexes.add(i);
                    }
                }
                return QuestionAnswerDTO.ofIndexes(indexes);
            }
            default: {
                throw new IllegalArgumentException(String.format("Could not parse question: '%s'", questionName));
            }
        }
    }
}
