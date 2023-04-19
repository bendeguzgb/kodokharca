package hu.bendeguz.kodokharca.service;

import hu.bendeguz.kodokharca.model.Color;
import hu.bendeguz.kodokharca.model.GameNumber;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class CombinationManager {

    private static int[] currentCombination = new int[0];
    private static Boolean[] value;
    private static int elementsInArray;

    public static List<List<GameNumber>> generateAllCombinations() {
        return generateAllCombinations(20, 5);
    }

    public static List<List<GameNumber>> generateAllCombinations(int elementsInArray) {
        return generateAllCombinations(20, elementsInArray);
    }

    public static List<List<GameNumber>> generateAllCombinations(int totalElementCount, int elementsInArray) {
        validateAndInitialize(totalElementCount, elementsInArray);
        List<List<GameNumber>> combinations = new ArrayList<>();

        for (int[] array = nextCombination(); array.length > 0; array = nextCombination()) {
            System.out.println(combinations.size());
            combinations.add(Arrays.stream(array)
                    .boxed()
                    .map(CombinationManager::mapIntegerToGameNumber)
                    .collect(Collectors.toList()));
        }

        return Collections.unmodifiableList(combinations);
    }

    private static int[] nextCombination() {
        if (currentCombination.length == 0) {
            initFirstCombination();
            return currentCombination.clone();
        }

        int[] current = currentCombination;
        List<Integer> leftover = new ArrayList<>();

        int index = current.length - 1;
        int nextNumber = -1;

        //Increment number if possible.
        //If not write -1
        while (nextNumber == -1 && index != -1) {
            int currentNumber = current[index];
            nextNumber = incrementNumber(currentNumber);

            current[index] = nextNumber;
            leftover.add(currentNumber);

            if (nextNumber == -1) {
                index--;
            }
        }

        //there are no more possible incrementation
        //[-1.0, -1.0, -1.0, -1.0, -1.0]
        if (index == -1) {
            return new int[0];
        }

        //restoring values from list
        for (Iterator<Integer> it = leftover.iterator(); it.hasNext();) {
            Integer d = it.next();
            value[d] = true;
            it.remove();
        }

        //set missing values by incrementing the previous
        //[0.0, 0.5, 3.0, -1., -1.]
        //[0.0, 0.5, 3.0, 3.5, -1.]
        //[0.0, 0.5, 3.0, 3.5, 4.0]
        for (int i = index + 1; i < current.length; i++) {
            int prevNumber = current[i-1];
            current[i] = incrementNumber(prevNumber);
        }

        return current.clone();
    }

    private static int incrementNumber(int x) {
        for (int i = x + 1; i < value.length; i++) {
            if(value[i]) {
                value[i] = false;
                return i;
            }
        }

        //when no bigger number left
        return -1;
    }

    private static void initFirstCombination() {
        currentCombination = new int[elementsInArray];
        for (int i = 0; i < currentCombination.length; i++) {
            value[i] = false;
            currentCombination[i] = i;
        }
    }

    private static void validateAndInitialize(int totalElementCountP, int elementsInArrayP) {
        if (totalElementCountP < 0 || elementsInArrayP < 0) {
            throw new IllegalArgumentException("The number of elements or combinations can not be less than 0!");
        }

        if (totalElementCountP < elementsInArrayP) {
            throw new IllegalArgumentException("The total number of elements can not be smaller than the number of combinations!");
        }

        if (elementsInArrayP < 3) {
            log.warn("If there are less than 3 numbers in the array then some asking some of the questions will result in an Exception!");
        }

        elementsInArray = elementsInArrayP;
        value = new Boolean[totalElementCountP];
        Arrays.fill(value, true);
        currentCombination = new int[0];
    }

    private static GameNumber mapIntegerToGameNumber(Integer value) {
        if (value / 2 == 5) {
            return new GameNumber(value / 2, Color.GREEN);
        }

        return new GameNumber(value / 2, ((value % 2 == 0) ? Color.WHITE : Color.BLACK));
    }
}
