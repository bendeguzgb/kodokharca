package hu.bendeguz.kodokharca.service;

import hu.bendeguz.kodokharca.model.Color;
import hu.bendeguz.kodokharca.model.GameNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CombinationFilterHandler {

    private static final String FILTER_NAME_VALUE_SEPARATOR = "-";

    public static List<List<GameNumber>> handleFiltering(List<List<GameNumber>> combinations, List<String> filters) {
        for (String filter : filters) {
            String[] splitFilter = filter.split(FILTER_NAME_VALUE_SEPARATOR);

            String filterName = splitFilter[0];
            String[] filterValues = Arrays.copyOfRange(splitFilter, 1, splitFilter.length);

            switch (filterName) {
                case "playerNumbers": {
                    String[] numbers = Arrays.copyOfRange(filterValues, 0, 5);
                    String[] colors = Arrays.copyOfRange(filterValues, 5, filterValues.length);
                    List<GameNumber> gameNumbers = new ArrayList<>();

                    for (int i = 0; i < numbers.length; i++) {
                        int number = Integer.parseInt(numbers[i]);
                        Color color = convertColorCharToColorEnum(colors[i]);
                        gameNumbers.add(new GameNumber(number, color));
                    }

                    combinations = CombinationFilter.playerNumbers(combinations, gameNumbers);
                    break;
                }

                case "numberLocation": {
                    Integer number = Integer.valueOf(filterValues[0]);
                    List<Integer> indexes = Arrays.stream(Arrays.copyOfRange(filterValues, 1, splitFilter.length))
                                                  .filter(Objects::nonNull)
                                                  .map(CombinationFilterHandler::parseIndex)
                                                  .collect(Collectors.toList());
                    combinations = CombinationFilter.containsNumber(combinations, number, indexes);
                    break;
                }

                case "sum": {
                    int sum = Integer.parseInt(filterValues[0]);
                    combinations = CombinationFilter.sumEqualsTo(combinations, sum);
                    break;
                }

                case "highestLowest": {
                    int difference = Integer.parseInt(filterValues[0]);
                    combinations = CombinationFilter.differenceOfHighestAndLowest(combinations, difference);
                    break;
                }

                case "leftSum": {
                    int leftSum = Integer.parseInt(filterValues[0]);
                    combinations = CombinationFilter.sumOfTheFirstThreeNumbers(combinations, leftSum);
                    break;
                }

                case "rightSum": {
                    int rightSum = Integer.parseInt(filterValues[0]);
                    combinations = CombinationFilter.sumOfTheLastThreeNumbers(combinations, rightSum);
                    break;
                }

                case "middleSum": {
                    int middleSum = Integer.parseInt(filterValues[0]);
                    combinations = CombinationFilter.sumOfMiddleNumbers(combinations, middleSum);
                    break;
                }

                case "evenOdd": {
                    int number = Integer.parseInt(filterValues[0]);
                    boolean isEven = Boolean.parseBoolean(filterValues[1]);
                    combinations = CombinationFilter.countOfEvenOrOddNumbers(combinations, number, isEven);
                    break;
                }

                case "blackWhiteCount": {
                    int count = Integer.parseInt(filterValues[0]);
                    boolean isBlack = Boolean.parseBoolean(filterValues[1]);
                    combinations = CombinationFilter.countOfBlackOrWhiteNumbers(combinations, count, isBlack);
                    break;
                }

                case "blackWhiteSum": {
                    int sum = Integer.parseInt(filterValues[0]);
                    boolean isBlack = Boolean.parseBoolean(filterValues[1]);
                    combinations = CombinationFilter.sumOfBlackOrWhiteNumbers(combinations, sum, isBlack);
                    break;
                }

                case "isCBigger":{
                    boolean isBigger = Boolean.parseBoolean(filterValues[0]);
                    combinations = CombinationFilter.cBiggerThan4(combinations, isBigger);
                    break;
                }

                case "sameNumberPairs": {
                    int count = Integer.parseInt(filterValues[0]);
                    combinations = CombinationFilter.countOfSameNumberPairs(combinations, count);
                    break;
                }

                case "sameColorNeighbour": {
                    List<Integer> indexes = Arrays.stream(filterValues)
                                                  .map(CombinationFilterHandler::parseIndex)
                                                  .collect(Collectors.toList());
                    combinations = CombinationFilter.neighbouringSameColorNumberPairs(combinations, indexes);
                    break;
                }

                case "consecutiveNeighbour": {
                    List<Integer> indexes = Arrays.stream(filterValues)
                                                  .map(CombinationFilterHandler::parseIndex)
                                                  .collect(Collectors.toList());
                    combinations = CombinationFilter.neighbouringConsecutiveNumberPairs(combinations, indexes);
                    break;
                }

                default: {
                    throw new IllegalArgumentException(String.format("Could not parse filter: '%s'", filterName));
                }
            }
        }
        return combinations;
    }

    private static Integer parseIndex(String index) {
        return Integer.parseInt(index) - 1;
    }

    private static Color convertColorCharToColorEnum(String c) {
        String value;

        switch (c.toLowerCase()) {
            case "h":
                value = "WHITE";
                break;
            case "k":
                value = "BLACK";
                break;
            case "z":
                value = "GREEN";
                break;
            default:
                throw new IllegalArgumentException(String.format("Could not parse color character: '%s'", c));
        }
        return Color.valueOf(value);
    }
}
