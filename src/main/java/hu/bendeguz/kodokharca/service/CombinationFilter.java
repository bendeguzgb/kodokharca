package hu.bendeguz.kodokharca.service;


import hu.bendeguz.kodokharca.model.Color;
import hu.bendeguz.kodokharca.model.GameNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CombinationFilter {

    public static List<List<GameNumber>> playerNumbers(List<List<GameNumber>> combinations, List<GameNumber> playerGameNumbers) {
        int playerGreenNumberCount = (int) playerGameNumbers.stream().filter(CombinationFilter::isGreenNumber).count();

        if (playerGreenNumberCount == 1) {
            Set<List<GameNumber>> resultSet = new LinkedHashSet<>();

            for (List<GameNumber> combination : combinations) {
                int greenCount = (int) combination.stream().filter(CombinationFilter::isGreenNumber).count();
                if (greenCount == 2) {
                    continue;
                }

                boolean shouldAdd = true;

                for (GameNumber playersGameNumber : playerGameNumbers) {
                    if (isGreenNumber(playersGameNumber)) {
                        continue;
                    }
                    if (combination.contains(playersGameNumber)) {
                        shouldAdd = false;
                        break;
                    }
                }

                if (shouldAdd) {
                    resultSet.add(combination);
                }
            }

            return new ArrayList<>(resultSet);
        }

        return applyListFilter(combinations, combination -> {
            for (GameNumber playersGameNumber : playerGameNumbers) {
                if (combination.contains(playersGameNumber)) {
                    return false;
                }
            }
            return true;
        });
    }

    public static List<List<GameNumber>> containsNumber(List<List<GameNumber>> combinations, Integer number, List<Integer> indexes) {
        return applyListFilter(combinations, combination -> {
            List<Integer> indexesCopy = new ArrayList<>(indexes);

            for (int i = 0; i < combination.size(); i++) {
                if (combination.get(i).getValue() == number) {
                    if (!indexesCopy.contains(i)) {
                        return false;
                    }
                    indexesCopy.remove(Integer.valueOf(i));
                }
            }

            return indexesCopy.size() == 0;
        });
    }

    public static List<List<GameNumber>> sumEqualsTo(List<List<GameNumber>> combinations, int sum) {
        return applyListFilter(combinations, combination ->
                combination.stream()
                        .mapToInt(GameNumber::getValue)
                        .sum() == sum);
    }

    public static List<List<GameNumber>> differenceOfHighestAndLowest(List<List<GameNumber>> combinations, int difference) {
        return applyListFilter(combinations, combination ->
                combination.get(combination.size() - 1).getValue() - combination.get(0).getValue() == difference);
    }

    public static List<List<GameNumber>> sumOfTheFirstThreeNumbers(List<List<GameNumber>> combinations, int sum) {
        return applyListFilter(combinations, combination ->
                sumOfSubList(combination, 0, 3) == sum);
    }

    public static List<List<GameNumber>> sumOfTheLastThreeNumbers(List<List<GameNumber>> combinations, int sum) {
        return applyListFilter(combinations, combination ->
                sumOfSubList(combination, combination.size() - 3, combination.size()) == sum);
    }

    public static List<List<GameNumber>> sumOfMiddleNumbers(List<List<GameNumber>> combinations, int sum) {
        return applyListFilter(combinations, combination -> {
            int middle = combination.size() / 2;

            if (combination.size() % 2 == 0) {
                return sumOfSubList(combination, middle - 1, middle + 1) == sum; // B+C
            }

            return sumOfSubList(combination, middle - 1, middle + 2) == sum; // B+C+D
        });
    }

    public static List<List<GameNumber>> countOfEvenOrOddNumbers(List<List<GameNumber>> combinations, int count, boolean isEven) {
        return applyListFilter(combinations, combination -> combination.stream()
            .map(GameNumber::getValue)
            .filter(number -> isEven == (number % 2 == 0))
            .count() == count);
    }

    public static List<List<GameNumber>> countOfBlackOrWhiteNumbers(List<List<GameNumber>> combinations, int count, boolean isBlack) {
        return applyListFilter(combinations, combination -> combination.stream()
            .filter(number -> !isGreenNumber(number) && isBlack  == isBlackNumber(number))
            .count() == count);
    }

    public static List<List<GameNumber>> sumOfBlackOrWhiteNumbers(List<List<GameNumber>> combinations, int sum, boolean isBlack) {
        return applyListFilter(combinations, combination -> combination.stream()
            .filter(number -> !isGreenNumber(number) && isBlack == isBlackNumber(number))
            .mapToInt(GameNumber::getValue)
            .sum() == sum);
    }

    public static List<List<GameNumber>> cBiggerThan4(List<List<GameNumber>> combinations, boolean isBigger) {
        return applyListFilter(combinations, combination -> isBigger == (combination.get(2).getValue() > 4));
    }

    /**
     * @param countOfPairs
     *          02468 -> 0
     *          00489 -> 1
     *          05599 -> 2
     */
    public static List<List<GameNumber>> countOfSameNumberPairs(List<List<GameNumber>> combinations, int countOfPairs) {
        return applyListFilter(combinations, combination -> {
            Map<Integer, Integer> map = new HashMap<>();

            for (GameNumber number : combination) {
                map.put(number.getValue(), map.getOrDefault(number.getValue(), 0) + 1);
            }

            return map.values()
                .stream()
                .filter(value -> value >= 2)
                .count() == countOfPairs;
        });
    }

    /**
     * @param numberIndexes
     *          02468 -> -
     *          02489 -> 3
     *          01589 -> 0-3
     *          01256 -> 0-1-3
     */
    public static List<List<GameNumber>> neighbouringSameColorNumberPairs(List<List<GameNumber>> combinations, List<Integer> numberIndexes) {
        return applyListFilter(combinations, combination -> {
            for (int i = 0; i < combination.size() - 1; i++) {
                if (numberIndexes.contains(i)) {
                    if (combination.get(i).getColor() != combination.get(i + 1).getColor()) {
                        return false;
                    }
                } else {
                    if (combination.get(i).getColor() == combination.get(i + 1).getColor()) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    /**
     * @param numberIndexes
     *          02468 -> -
     *          02489 -> 3
     *          01589 -> 0-3
     *          01256 -> 0-1-3
     */
    public static List<List<GameNumber>> neighbouringConsecutiveNumberPairs(List<List<GameNumber>> combinations, List<Integer> numberIndexes) {
        return applyListFilter(combinations, combination -> {
            for (int i = 0; i < combination.size() - 1; i++) {
                if (numberIndexes.contains(i)) {
                    if (combination.get(i).getValue() + 1 != combination.get(i + 1).getValue()) {
                        return false;
                    }
                } else {
                    if (combination.get(i).getValue() + 1 == combination.get(i + 1).getValue()) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    /**
     * @param from Inclusive.
     * @param to   Exclusive.
     */
    private static int sumOfSubList(List<GameNumber> combination, int from, int to) {
        if (from < 0 || to > combination.size() || from > to) {
            throw new IllegalArgumentException(String.format("Illegal arguments found! from='%d' to='%d'", from, to));
        }

        return combination.subList(from, to)
                .stream()
                .mapToInt(GameNumber::getValue)
                .sum();
    }

    private static<T> List<T> applyListFilter(List<T> list, Predicate<T> predicate) {
        return list.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private static boolean isBlackNumber(GameNumber gameNumber) {
        return gameNumber.getColor() == Color.BLACK;
    }

    private static boolean isWhiteNumber(GameNumber gameNumber) {
        return gameNumber.getColor() == Color.WHITE;
    }

    private static boolean isGreenNumber(GameNumber gameNumber) {
        return gameNumber.getColor() == Color.GREEN;
    }
}
