package hu.bendeguz.kodokharca.service;

import hu.bendeguz.kodokharca.model.GameNumber;
import java.util.List;

public class Utils {
    
    /**
     * @param from Inclusive.
     * @param to   Exclusive.
     */
    public static int sumOfSubList(List<GameNumber> combination, int from, int to) {
        if (from < 0 || to > combination.size() || from > to) {
            throw new IllegalArgumentException(String.format("Illegal arguments found! from='%d' to='%d'", from, to));
        }
        
        return combination.subList(from, to)
            .stream()
            .mapToInt(GameNumber::getValue)
            .sum();
    }
}
