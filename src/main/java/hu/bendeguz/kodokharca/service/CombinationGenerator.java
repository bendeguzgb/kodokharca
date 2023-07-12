package hu.bendeguz.kodokharca.service;

import hu.bendeguz.kodokharca.model.Color;
import hu.bendeguz.kodokharca.model.GameNumber;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CombinationGenerator {

    private static List<List<GameNumber>> combinationsRange20Numbers4 = null;
    private static List<List<GameNumber>> combinationsRange20Numbers5 = null;

    static {
        generateAllCombinations(4);
        generateAllCombinations(5);
    }


    public static List<List<GameNumber>> generateAllCombinations() {
        return generateAllCombinations(20, 5);
    }

    public static List<List<GameNumber>> generateAllCombinations(int elementsInArray) {
        return generateAllCombinations(20, elementsInArray);
    }

    public static List<List<GameNumber>> generateAllCombinations(int range, int combinationSize) {
        if (range == 20) {
            if (combinationSize == 4 && combinationsRange20Numbers4 != null) {
                return combinationsRange20Numbers4;
            }

            if (combinationSize == 5 && combinationsRange20Numbers5 != null) {
                return combinationsRange20Numbers5;
            }
        }

        validateParameters(range, combinationSize);

        List<List<GameNumber>> combinations = new ArrayList<>();
        int[] combination = new int[combinationSize];

        for (int i = 0; i < combinationSize; i++) {
            combination[i] = i;
        }

        while (combination[combinationSize - 1] < range) {
            combinations.add(toGameNumbers(combination));

            // Find the rightmost incrementable number
            int lastIncrementableIndex = combinationSize - 1;

            while (lastIncrementableIndex != 0
                && combination[lastIncrementableIndex] == range - combinationSize + lastIncrementableIndex) {
                lastIncrementableIndex--;
            }

            // Increment number(s)
            combination[lastIncrementableIndex]++;

            for (int i = lastIncrementableIndex + 1; i < combinationSize; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }

        combinations = Collections.unmodifiableList(combinations);

        if (range == 20) {
            if (combinationSize == 4 && combinationsRange20Numbers4 == null) {
                combinationsRange20Numbers4 = combinations;
                return combinationsRange20Numbers4;
            }

            if (combinationSize == 5 && combinationsRange20Numbers5 == null) {
                combinationsRange20Numbers5 = combinations;
                return combinationsRange20Numbers5;
            }
        }
        return combinations;
    }

    private static void validateParameters(int totalElementCountP, int elementsInArrayP) {
        if (totalElementCountP < 0 || elementsInArrayP < 0) {
            throw new IllegalArgumentException("The number of elements or combinations can not be less than 0!");
        }

        if (totalElementCountP < elementsInArrayP) {
            throw new IllegalArgumentException("The total number of elements can not be smaller than the number of combinations!");
        }

        if (elementsInArrayP < 3) {
            log.warn("If there are less than 3 numbers in the array then some asking some of the questions will result in an Exception!");
        }
    }

    private static List<GameNumber> toGameNumbers(int[] numbers) {
        return Arrays.stream(numbers).boxed()
            .map(CombinationGenerator::mapIntegerToGameNumber)
            .collect(Collectors.toUnmodifiableList());
    }

    private static GameNumber mapIntegerToGameNumber(Integer value) {
        if (value / 2 == 5) {
            return new GameNumber(value / 2, Color.GREEN);
        }

        return new GameNumber(value / 2, ((value % 2 == 0) ? Color.WHITE : Color.BLACK));
    }
}
