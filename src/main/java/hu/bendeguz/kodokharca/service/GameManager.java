package hu.bendeguz.kodokharca.service;

import hu.bendeguz.kodokharca.model.GameDetails;
import hu.bendeguz.kodokharca.model.Player;
import hu.bendeguz.kodokharca.model.Question;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameManager {

    // This list is autowired. See QuestionLoaderConfig class
    private final List<String> questionIds;
    
    
    public GameDetails manageGame(int opponentCount, String code, List<Question> playersQuestions) {
        GameDetails gameDetails = new GameDetails(opponentCount, code, questionIds);
        
        // Used when illegal or invalid question found
        GameDetails backupGameDetails = new GameDetails(opponentCount, code, questionIds);
        
        System.out.println(gameDetails.getAvailableQuestions());
        
        for (Question playersQuestion : playersQuestions) {
            List<Question> askedQuestions = new ArrayList<>();
            
            for (Player currentPlayer : gameDetails.getPlayers()) {
                Question choosenQuestion =
                    (currentPlayer.isHuman() ? playersQuestion : npcChooseQuestions(currentPlayer, gameDetails));
                
                if (gameDetails.getCurrentPlayer().isHuman() && !gameDetails.isAvailableQuestion(choosenQuestion)) {
                    // todo: Handle when human player asks a question which is not available.
                    
                }
                
                gameDetails.askQuestion(choosenQuestion);
                askedQuestions.add(choosenQuestion);
            }
            
            for (Question askedQuestion : askedQuestions) {
                backupGameDetails.askQuestion(askedQuestion);
            }
            
            // More playerQuestion can be provided, by editing the url,
            // even after the game has technically ended
            if (gameDetails.isGameOver()) {
                return gameDetails;
            }
        }
        
        Player currentPlayer;
        
        while (!(currentPlayer = gameDetails.getCurrentPlayer()).isHuman()) {
            Question choosenQuestion = npcChooseQuestions(currentPlayer, gameDetails);
            gameDetails.askQuestion(choosenQuestion);
        }
        
//        if (!gameDetails.getCurrentPlayer().isHuman()) {
//            npcsAskQuestions(gameDetails);
//        }
//
//        for (Question playersQuestion : playersQuestions) {
//            if (!gameDetails.isAvailableQuestion(playersQuestion)) {
//                // TODO: 2023. 10. 17. Handle when human player asks a question which is not available.
//                //  (Maybe with redirect to previously correct state?)
//                List<String> availableQuestions = gameDetails.getAvailableQuestions();
//                String strList = String.join(", ", availableQuestions);
//                String errorMessage = String.format("Questions: '%s' is not available: [%s]", playersQuestion, strList);
//
//                log.error(errorMessage);
//            }
//
//            gameDetails.askQuestion(humanPlayer, playersQuestion);
//
//            npcsAskQuestions(gameDetails);
//        }
        
        return gameDetails;
    }
    
    private Question npcChooseQuestions(Player nonHumanPlayer, GameDetails gameDetails) {
        List<String> availableQuestions = gameDetails.getAvailableQuestions();
        List<String> preparedQuestions = Question.prepareQuestions(availableQuestions);
        
        String chosenQuestionName = nonHumanPlayer.chooseQuestion(preparedQuestions, questionIds);
        
        return Question.ofName(chosenQuestionName);
    }
    
    private void npcsAskQuestions(GameDetails gameDetails) {
        Player currentPlayer;
        
        while (!(currentPlayer = gameDetails.getCurrentPlayer()).isHuman()) {
            Question question = npcChooseQuestions(currentPlayer, gameDetails);

            gameDetails.askQuestion(question);
        }
    }
}
