package hu.bendeguz.kodokharca.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuestionsAndFilteringTypes {
    
    EVEN_ODD("evenOdd"),
    BLACK_WHITE_COUNT("blackWhiteCount"),
    BLACK_WHITE_SUM("blackWhiteSum"),
    PLAYER_NUMBERS("playerNumbers"),
    GUESS("guess"),
    BLACK_COUNT("blackCount"),
    BLACK_SUM("blackSum"),
    CONSECUTIVE_NEIGHBOUR("consecutiveNeighbour"),
    EVEN("even"),
    HIGHEST_LOWEST("highestLowest"),
    IS_C_BIGGER("isCBigger"),
    LEFT_SUM("leftSum"),
    MIDDLE_SUM("middleSum"),
    NUMBER_LOCATION("numberLocation"),
    ODD("odd"),
    RIGHT_SUM("rightSum"),
    SAME_COLOR_NEIGHBOUR("sameColorNeighbour"),
    SAME_NUMBER_PAIRS("sameNumberPairs"),
    SUM("sum"),
    WHITE_COUNT("whiteCount"),
    WHITE_SUM("whiteSum");
    
    private final String name;
    
    public static QuestionsAndFilteringTypes ofName(String name) {
        for (QuestionsAndFilteringTypes value : values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("No enum constant '%s'", name));
    }
}
