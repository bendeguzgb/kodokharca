package hu.bendeguz.kodokharca.model;

import static hu.bendeguz.kodokharca.service.CombinationGenerator.generateRandomCombinationsForPlayers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.bendeguz.kodokharca.service.question.QuestionHandler;
import hu.bendeguz.kodokharca.service.question.GameCodeGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
public class GameDetails {
    
    private static final int MAX_QUESTIONS_PER_TURN = 6;
    private static final String SUCCESSFUL_GUESS_TEXT = "Sikeres tippelés!";
    private static final String FAILED_GUESS_TEXT = "Sikertelen tippelés!";
    
    @JsonIgnore
    private final Random random;
    private boolean isGameOver = false;
    
    //
    private String errorMessage = null;
    
    private final int playerCount;
    private final int humanPlayerIndex;
    private final List<Player> players;
    private final List<String> shuffledQuestions;
    private final List<Question> askedQuestions = new ArrayList<>();
    private final List<GameNumber> leftOverGameNumbers;
    
    // Used for showing what happened in the UI. Order of the elements is necessary.
    private final Map<Question, List<String>> allQuestionsAndAnswers = new LinkedHashMap<>();
    private final Map<Question, List<String>> humanPlayersQuestionsAndAnswers = new LinkedHashMap<>();
    
    // Used by player's to choose question. Order of the elements is probably not necessary.
    // questionsAskedAndAnswers | key: player.position | value: Question asked about this player and the Answer
    private final Map<Integer, Map<Question, String>> questionsAskedAndAnswers = new LinkedHashMap<>();
    
    
    public GameDetails(int opponentCount, String code, List<String> questionIds) {
        this.playerCount = opponentCount + 1;
        this.random = GameCodeGenerator.createRandom(playerCount, code);
        this.humanPlayerIndex = random.nextInt(playerCount);
        
        questionIds = new ArrayList<>(questionIds);
        Collections.shuffle(questionIds, random);
        this.shuffledQuestions = Collections.unmodifiableList(questionIds);
        
        List<List<GameNumber>> combinations = generateRandomCombinationsForPlayers(playerCount, random);
        
        List<Player> players = new ArrayList<>();
        
        for (int i = 0; i < playerCount; i++) {
            questionsAskedAndAnswers.put(i, new LinkedHashMap<>());
            
            List<GameNumber> randomCombination = combinations.get(i);
            
            Player player = Player.builder()
                .position(i)
                .gameNumbers(randomCombination)
                .questionsAskedAndAnswers(questionsAskedAndAnswers)
                .isHuman(i == humanPlayerIndex)
                .build();
            
            players.add(player);
        }
        
        leftOverGameNumbers = combinations.get(combinations.size() - 1);
        
        this.players = Collections.unmodifiableList(players);
    }
    
    /**
     *
     * @return The {@link Player} who should ask the next question.
     */
    public Player getCurrentPlayer() {
        int index = askedQuestions.size() % playerCount;
        return players.get(index);
    }
    
    public List<Player> getOtherPlayers(Player currentPlayer) {
        List<Player> otherPlayers = new ArrayList<>();
        
        for (Player player : players) {
            if (!player.equals(currentPlayer)) {
                otherPlayers.add(player);
            }
        }
        return Collections.unmodifiableList(otherPlayers);
    }
    
    public void askQuestion(Question question) {
        Player askingPlayer = getCurrentPlayer();
        
        List<Player> otherPlayers = getOtherPlayers(askingPlayer);
        List<String> answersWithPlayerPosition = new ArrayList<>();
        
        if (question.getId().equals(QuestionsAndFilteringTypes.GUESS.getName())) {
            List<GameNumber> solution = getSolutionForPlayer(askingPlayer);
            String answer = QuestionHandler.askQuestion(question.getName(), solution).getValue();
            isGameOver = Boolean.parseBoolean(answer);
            
            String guessAnswer = (isGameOver) ? SUCCESSFUL_GUESS_TEXT : FAILED_GUESS_TEXT;
            answersWithPlayerPosition.add(guessAnswer);
        } else {
            for (Player otherPlayer : otherPlayers) {
                String answer = QuestionHandler.askQuestion(question.getName(), otherPlayer.getGameNumbers()).getValue();
                String answerWithPlayerPosition = String.format("J%d: %s", otherPlayer.getPosition() + 1, answer);
                
                Map<Question, String> questionAnswerMap = questionsAskedAndAnswers.get(otherPlayer.getPosition());
                questionAnswerMap.put(question, answer);
                
                answersWithPlayerPosition.add(answerWithPlayerPosition);
            }
        }
        
        askedQuestions.add(question);
        allQuestionsAndAnswers.put(question, answersWithPlayerPosition);
        
        if (askingPlayer.isHuman()) {
            humanPlayersQuestionsAndAnswers.put(question, answersWithPlayerPosition);
        }
    }
    
    public List<String> getAvailableQuestions() {
        List<String> shuffledQuestionsCopy = new ArrayList<>(shuffledQuestions);
        
        for (Question question : askedQuestions) {
            shuffledQuestionsCopy.remove(question.getId());
        }
        
        return shuffledQuestionsCopy.subList(0, Math.min(MAX_QUESTIONS_PER_TURN, shuffledQuestionsCopy.size()));
    }
    
    public boolean isAvailableQuestion(Question question) {
        return question.getId().equals(QuestionsAndFilteringTypes.GUESS.getName())
                || getAvailableQuestions().contains(question.getId());
    }
    
    public Player getHumanPlayer() {
        return players.get(humanPlayerIndex);
    }
    
    private List<GameNumber> getSolutionForPlayer(Player askingPlayer) {
        if (playerCount == 2) {
            return getOtherPlayers(askingPlayer).get(0).getGameNumbers();
        }
        return leftOverGameNumbers;
    }
}
