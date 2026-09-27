package hu.bendeguz.kodokharca.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Represents the id of the question, the question chosen and the text of the question.
 * The id is the same as the file name and the form id.
 * The name is the ask-able version of the id. It necessary at numberLocation-## questions.
 *
 * e.g.:
 *      sum - sum - Mennyi a számjegyek összege?
 *      numberLocation-0 - numberLocation-0 - Hol vannak a #0-ás számjegyek?
 *      numberLocation-12 - numberLocation-2 - Hol vannak a #2-es számjegyek?
 */
@Data
@Setter(AccessLevel.NONE)
public class Question {
    
    private static final String NUMBER_LOCATION_NAME = QuestionsAndFilteringTypes.NUMBER_LOCATION.getName();
    private static final String GUESS_NAME = QuestionsAndFilteringTypes.GUESS.getName();
    
    // TODO: 2023. 10. 17. QuestionLoaderConfig "injection"
    // This value is set in QuestionLoaderConfig. I hate it, but I don't have a better idea right now
    public static Map<String, String> questionIdsAndTexts;
    
    private final String id;
    private final String name;
    private final String text;
    
    
    public Question(String id, String name) {
        this.id = id;
        this.name = name;
        text = getText(id, name);
    }
    
    public static Question ofName(String name) {
        if (name.startsWith(GUESS_NAME)) {
            return new Question(GUESS_NAME, name);
        }
        
        if (!name.startsWith(NUMBER_LOCATION_NAME)) {
            return new Question(name, name);
        }
        
        int number = Integer.parseInt(String.valueOf(name.charAt(name.length() - 1)));
        
        // numberLocation-0 -> numberLocation-0 - numberLocation-0
        if (number == 0 || number == 5) {
            return new Question(name, name);
        }
        
        // numberLocation-1 -> numberLocation-12 - numberLocation-1
        if (number == 1 || number == 3 || number == 6 || number == 8) {
            String id = String.format("%s-%d%d", NUMBER_LOCATION_NAME, number, number + 1);
            return new Question(id, name);
        }
        
        // numberLocation-2 -> numberLocation-12 - numberLocation-2
        String id = String.format("%s-%d%d", NUMBER_LOCATION_NAME, number - 1, number);
        return new Question(id, name);
    }
    
    public static List<Question> ofNames(List<String> names) {
        List<Question> questions = new ArrayList<>();
        
        for (String name : names) {
            questions.add(ofName(name));
        }
        return questions;
    }
    
    private static String getText(String id, String name) {
        if (id.equals(GUESS_NAME)) {
            return questionIdsAndTexts.get(GUESS_NAME) + name.replace(GUESS_NAME + "-", "");
        }
        
        if (!id.startsWith(NUMBER_LOCATION_NAME)) {
            return questionIdsAndTexts.get(id);
        }
        
        int number = Character.getNumericValue(name.charAt(name.length() - 1));
        
        if (number == 0 || number == 5) {
            return questionIdsAndTexts.get(id);
        }
        
        String s;
        
        switch (number) {
            case 1:
                s = "az #1-es";
                break;
            case 2:
            case 4:
            case 7:
            case 9:
                s = "a #" + number + "-es";
                break;
            case 3:
                s = "a #3-mas";
                break;
            case 6:
                s = "a #6-os";
                break;
            case 8:
                s = "a #8-as";
                break;
            default:
                throw new IllegalArgumentException(String.format("WTF is this: '%d'??", number));
        }
        
        return String.format("Hol vannak %s számjegyek?", s);
    }
    
    public static List<String> prepareQuestion(String question) {
        List<String> preparedQuestion = new ArrayList<>();
        
            // It is only necessary prepare questions where 2 numbers can be chosen
            // numberLocation-0 -> numberLocation-0  // nothing happens
            // numberLocation-23 -> numberLocation-2, numberLocation-3  // turns 1 question into 2
            String format = String.format("%s-\\d\\d", NUMBER_LOCATION_NAME);
            
            if (!question.matches(format)) {
                preparedQuestion.add(question);
                return preparedQuestion;
            }
            
            String question1 = String.format("%s-%s", NUMBER_LOCATION_NAME, question.charAt(question.length() - 2));
            String question2 = String.format("%s-%s", NUMBER_LOCATION_NAME, question.charAt(question.length() - 1));
            
            preparedQuestion.add(question1);
            preparedQuestion.add(question2);
        
        return preparedQuestion;
    }
    
    public static List<String> prepareQuestions(List<String> questions) {
        List<String> preparedQuestions = new ArrayList<>();
        
        for (String question : questions) {
            preparedQuestions.addAll(prepareQuestion(question));
        }
        return preparedQuestions;
    }
}
