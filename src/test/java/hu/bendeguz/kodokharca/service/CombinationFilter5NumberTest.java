package hu.bendeguz.kodokharca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import hu.bendeguz.kodokharca.model.Color;
import hu.bendeguz.kodokharca.model.GameNumber;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CombinationFilter5NumberTest {

    private static final int ALL_COMBINATIONS_COUNT = 15504; //  20! / (5! * 15!)
    private static final int TOTAL_ELEMENT_COUNT = 20;
    private static final int ELEMENTS_IN_ARRAY = 5;
    private static final List<List<GameNumber>> combinations = CombinationManager.generateAllCombinations(TOTAL_ELEMENT_COUNT, ELEMENTS_IN_ARRAY);
    private static final List<List<GameNumber>> customCombinations = new ArrayList<>();

    @BeforeAll
    static void setup() {
        customCombinations.add(List.of(
            new GameNumber(1, Color.WHITE),
            new GameNumber(1, Color.BLACK),
            new GameNumber(3, Color.BLACK),
            new GameNumber(6, Color.WHITE),
            new GameNumber(8, Color.BLACK)));

        customCombinations.add(List.of(
            new GameNumber(1, Color.WHITE),
            new GameNumber(1, Color.BLACK),
            new GameNumber(5, Color.GREEN),
            new GameNumber(7, Color.WHITE),
            new GameNumber(9, Color.BLACK)));

        customCombinations.add(List.of(
            new GameNumber(1, Color.WHITE),
            new GameNumber(1, Color.BLACK),
            new GameNumber(5, Color.GREEN),
            new GameNumber(7, Color.WHITE),
            new GameNumber(9, Color.BLACK)));

        customCombinations.add(List.of(
            new GameNumber(1, Color.WHITE),
            new GameNumber(5, Color.GREEN),
            new GameNumber(5, Color.GREEN),
            new GameNumber(6, Color.WHITE),
            new GameNumber(7, Color.BLACK)));
    }

    @Test
    void filterEveryPlayerCombinationTest() {
        long start = OffsetDateTime.now().toEpochSecond();

        for (int i = 0; i < combinations.size(); i++) {
            if (i % 1550 == 0) {
                System.out.println(i / 1550 + "0% TESTED");
            }
            int size = CombinationFilter.playerNumbers(combinations, combinations.get(i)).size();

            assertEquals(3003, size);
        }

        System.out.println("\nFinished in " + (OffsetDateTime.now().toEpochSecond() - start) + " seconds");
    }

    @Test
    void whenPlayerHas1GreenNumbersItShouldFilterDuplicateGreenCombinationsTest() {
        List<GameNumber> playersNumbers = List.of(
            new GameNumber(0, Color.WHITE),
            new GameNumber(0, Color.BLACK),
            new GameNumber(2, Color.WHITE),
            new GameNumber(2, Color.BLACK),
            new GameNumber(5, Color.GREEN)
        );

        List<List<GameNumber>> results = CombinationFilter.playerNumbers(customCombinations, playersNumbers);
        int size = results.size();

        assertEquals(2, size);
        assertEquals(3, results.get(0).get(2).getValue());
        assertEquals(5, results.get(1).get(2).getValue());
    }

    @Test
    void whenPlayerHas2GreenNumbersItShouldFilterAllGreenCombinationsTest() {
        List<GameNumber> playersNumbers = List.of(
            new GameNumber(0, Color.WHITE),
            new GameNumber(0, Color.BLACK),
            new GameNumber(2, Color.WHITE),
            new GameNumber(5, Color.GREEN),
            new GameNumber(5, Color.GREEN)
        );

        List<List<GameNumber>> results = CombinationFilter.playerNumbers(customCombinations, playersNumbers);

        assertEquals(1, results.size());
        assertEquals(3, results.get(0).get(2).getValue());
    }

    @Test
    void filterEveryNumberTest() {
        List<List<Integer>> indexes = new ArrayList<>();
        indexes.add(List.of());
        indexes.add(List.of(0));
        indexes.add(List.of(1));
        indexes.add(List.of(2));
        indexes.add(List.of(3));
        indexes.add(List.of(4));
        indexes.add(List.of(0, 1));
        indexes.add(List.of(1, 2));
        indexes.add(List.of(2, 3));
        indexes.add(List.of(3, 4));

        for (int i = 0; i < 10; i++) {
            int sum = 0;
            for (List<Integer> index : indexes) {
                int size = CombinationFilter.containsNumber(combinations, i, index).size();
                sum += size;
            }
            assertEquals(ALL_COMBINATIONS_COUNT, sum);
        }
    }

    @Test
    void whenFilteringForOneNumber1AtTheFirstPlaceItShouldFilterEveryOtherCombination() {
        List<List<GameNumber>> results = CombinationFilter.containsNumber(customCombinations, 1, List.of(0));

        assertEquals(1, results.size());
        assertEquals(7, results.get(0).get(4).getValue());
    }

    @Test
    void whenFilteringForOneNumber5AtThe3rdPlaceItShouldFilterEveryOtherCombination() {
        List<List<GameNumber>> results = CombinationFilter.containsNumber(customCombinations, 5, List.of(2));

        assertEquals(2, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
    }

    @Test
    void filterEverySumTest() {
        int sum = 0;
        for (int i = 4; i < 42; i++) {
            int size = CombinationFilter.sumEqualsTo(combinations, i).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForSumEqualingTo24ItShouldOnlyHaveOneResult() {
        List<List<GameNumber>> results = CombinationFilter.sumEqualsTo(customCombinations, 24);

        assertEquals(1, results.size());
        assertEquals(7, results.get(0).get(4).getValue());
    }

    @Test
    void whenFilteringForSumEqualingTo15ItShouldHaveNoResults() {
        List<List<GameNumber>> results = CombinationFilter.sumEqualsTo(customCombinations, 15);

        assertEquals(0, results.size());
    }

    @Test
    void filterEveryDifferenceOfHighestAndLowestTest() {
        int sum = 0;
        for (int i = 2; i < 10; i++) {
            int size = CombinationFilter.differenceOfHighestAndLowest(combinations, i).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForDifferenceOfHighestAndLowestOf8ItShouldHave2Results() {
        List<List<GameNumber>> results = CombinationFilter.differenceOfHighestAndLowest(customCombinations, 8);

        assertEquals(2, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
    }

    @Test
    void whenFilteringForDifferenceOfHighestAndLowestOf10ItShouldHaveNoResult() {
        List<List<GameNumber>> results = CombinationFilter.differenceOfHighestAndLowest(customCombinations, 10);

        assertEquals(0, results.size());
    }

    @Test
    void filterEverySumOfTheFirstThreeNumbersTest() {
        int sum = 0;
        for (int i = 1; i < 24; i++) {
            int size = CombinationFilter.sumOfTheFirstThreeNumbers(combinations, i).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForSumOfFirstThreeNumberOf5ItShouldHave1Result() {
        List<List<GameNumber>> results = CombinationFilter.sumOfTheFirstThreeNumbers(customCombinations, 5);

        assertEquals(1, results.size());
        assertEquals(3, results.get(0).get(2).getValue());
    }

    @Test
    void whenFilteringForSumOfFirstThreeNumberOf20ItShouldHaveNoResult() {
        List<List<GameNumber>> results = CombinationFilter.sumOfTheFirstThreeNumbers(customCombinations, 20);

        assertEquals(0, results.size());
    }

    @Test
    void filterEverySumOfTheLastThreeNumbersTest() {
        int sum = 0;
        for (int i = 4; i < 27; i++) {
            int size = CombinationFilter.sumOfTheLastThreeNumbers(combinations, i).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForSumOfLastThreeNumberOf21ItShouldHave2Results() {
        List<List<GameNumber>> results = CombinationFilter.sumOfTheLastThreeNumbers(customCombinations, 21);

        assertEquals(2, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
    }

    @Test
    void whenFilteringForSumOfLastThreeNumberOf20ItShouldHaveNoResult() {
        List<List<GameNumber>> results = CombinationFilter.sumOfTheLastThreeNumbers(customCombinations, 20);

        assertEquals(0, results.size());
    }

    @Test
    void filterEverySumOfTheMiddleNumbersTest() {
        int sum = 0;
        for (int i = 2; i < 26; i++) {
            int size = CombinationFilter.sumOfMiddleNumbers(combinations, i).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForTheMiddleThreeNumbersOf16ItShouldHave1Result() {
        List<List<GameNumber>> results = CombinationFilter.sumOfMiddleNumbers(customCombinations, 16);

        assertEquals(1, results.size());
        assertEquals(7, results.get(0).get(4).getValue());
    }

    @Test
    void whenFilteringForTheMiddleThreeNumbersOf20ItShouldHaveNoResults() {
        List<List<GameNumber>> results = CombinationFilter.sumOfMiddleNumbers(customCombinations, 20);

        assertEquals(0, results.size());
    }

    @Test
    void filterEveryEvenOrOddNumbersTest() {
        for (int i = 0; i < 2; i++) {
            int sum = 0;
            for (int j = 0; j < 6; j++) {
                int size = CombinationFilter.countOfEvenOrOddNumbers(combinations, j, i % 2 == 0).size();
                sum += size;
            }
            assertEquals(ALL_COMBINATIONS_COUNT, sum);
        }
    }

    @Test
    void whenFilteringFor5OddNumbersItShouldHave2Results() {
        List<List<GameNumber>> results = CombinationFilter.countOfEvenOrOddNumbers(customCombinations, 5, false);

        assertEquals(2, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
    }

    @Test
    void whenFilteringFor0EvenNumbersItShouldHave2Results() {
        List<List<GameNumber>> results = CombinationFilter.countOfEvenOrOddNumbers(customCombinations, 0, true);

        assertEquals(2, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
    }


    @Test
    void filterEveryContOfBlackOrWhiteNumbersTest() {
        for (int i = 0; i < 2; i++) {
            int sum = 0;
            for (int j = 0; j < 6; j++) {
                int size = CombinationFilter.countOfBlackOrWhiteNumbers(combinations, j, i % 2 == 0).size();
                sum += size;
            }
            assertEquals(ALL_COMBINATIONS_COUNT, sum);
        }
    }

    @Test
    void whenFilteringFor2WhiteNumbersItShouldHave4Results() {
        List<List<GameNumber>> results = CombinationFilter.countOfBlackOrWhiteNumbers(customCombinations, 2, false);

        assertEquals(4, results.size());
    }


    @Test
    void whenFilteringFor2BlackItShouldHave2Results() {
        List<List<GameNumber>> results = CombinationFilter.countOfBlackOrWhiteNumbers(customCombinations, 2, true);

        assertEquals(2, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
        assertEquals(9, results.get(1).get(4).getValue());
    }

    @Test
    void filterEverySumOfBlackOrWhiteNumbersTest() {
        for (int i = 0; i < 2; i++) {
            int sum = 0;
            for (int j = 0; j < 35; j++) {
                int size = CombinationFilter.sumOfBlackOrWhiteNumbers(combinations, j, i % 2 == 0).size();
                sum += size;
            }
            assertEquals(ALL_COMBINATIONS_COUNT, sum);
        }
    }

    @Test
    void whenFilteringSumOfBlackNumbersOf12ItShouldHave1Result() {
        List<List<GameNumber>> results = CombinationFilter.sumOfBlackOrWhiteNumbers(customCombinations, 12, true);

        assertEquals(1, results.size());
        assertEquals(8, results.get(0).get(4).getValue());
    }

    @Test
    void whenFilteringSumOfBlackNumbersOf8ItShouldHave1Result() {
        List<List<GameNumber>> results = CombinationFilter.sumOfBlackOrWhiteNumbers(customCombinations, 8, false);

        assertEquals(2, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
        assertEquals(9, results.get(1).get(4).getValue());
    }

    @Test
    void filterEveryIsCBiggerThan4Test() {
        int sum = 0;
        for (int i = 0; i < 2; i++) {
            int size = CombinationFilter.cBiggerThan4(combinations, i % 2 == 0).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForCIsBiggerThan4ItShouldHave3Results() {
        List<List<GameNumber>> results = CombinationFilter.cBiggerThan4(customCombinations, true);

        assertEquals(3, results.size());
        assertEquals(9, results.get(0).get(4).getValue());
        assertEquals(9, results.get(1).get(4).getValue());
        assertEquals(7, results.get(2).get(4).getValue());
    }

    @Test
    void whenFilteringForCIsNotBiggerThan4ItShouldHave1Result() {
        List<List<GameNumber>> results = CombinationFilter.cBiggerThan4(customCombinations, false);

        assertEquals(1, results.size());
        assertEquals(8, results.get(0).get(4).getValue());
    }

    @Test
    void filterEverySameNumberPairTest() {
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            int size = CombinationFilter.countOfSameNumberPairs(combinations, i).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForSameNumberOfPairsOf1ItShouldHave4Results() {
        List<List<GameNumber>> results = CombinationFilter.countOfSameNumberPairs(customCombinations, 1);

        assertEquals(4, results.size());
        assertEquals(8, results.get(0).get(4).getValue());
        assertEquals(9, results.get(1).get(4).getValue());
        assertEquals(9, results.get(2).get(4).getValue());
        assertEquals(7, results.get(3).get(4).getValue());
    }

    @Test
    void whenFilteringForSameNumberOfPairsOf0ItShouldHave0Result() {
        List<List<GameNumber>> results = CombinationFilter.countOfSameNumberPairs(customCombinations, 0);

        assertEquals(0, results.size());
    }

    @Test
    void filterEveryNeighbouringSameColorNumberPairsTest() {
        List<List<Integer>> indexes = new ArrayList<>();
        indexes.add(List.of());
        indexes.add(List.of(0));
        indexes.add(List.of(1));
        indexes.add(List.of(2));
        indexes.add(List.of(3));
        indexes.add(List.of(0, 1));
        indexes.add(List.of(0, 2));
        indexes.add(List.of(0, 3));
        indexes.add(List.of(1, 2));
        indexes.add(List.of(1, 3));
        indexes.add(List.of(2, 3));
        indexes.add(List.of(0, 1, 2));
        indexes.add(List.of(0, 1, 3));
        indexes.add(List.of(0, 2, 3));
        indexes.add(List.of(1, 2, 3));
        indexes.add(List.of(0, 1, 2, 3));

        int sum = 0;
        for (List<Integer> index : indexes) {
            int size = CombinationFilter.neighbouringSameColorNumberPairs(combinations, index).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForNeighbouringSameColorNumberPairsOfAt2ndPlaceItShouldHave2Results() {
        List<List<GameNumber>> results = CombinationFilter.neighbouringSameColorNumberPairs(customCombinations, List.of(1));

        assertEquals(2, results.size());
        assertSame(results.get(0).get(1).getColor(), results.get(0).get(2).getColor());
        assertSame(results.get(1).get(1).getColor(), results.get(1).get(2).getColor());
    }

    @Test
    void whenFilteringForNoNeighbouringSameColorNumberPairsItShouldHave2Results() {
        List<List<GameNumber>> results = CombinationFilter.neighbouringSameColorNumberPairs(customCombinations, List.of());

        assertEquals(2, results.size());
    }

    @Test
    void filterEveryNeighbouringConsecutiveNumberPairsTest() {
        List<List<Integer>> indexes = new ArrayList<>();
        indexes.add(List.of());
        indexes.add(List.of(0));
        indexes.add(List.of(1));
        indexes.add(List.of(2));
        indexes.add(List.of(3));
        indexes.add(List.of(0, 1));
        indexes.add(List.of(0, 2));
        indexes.add(List.of(0, 3));
        indexes.add(List.of(1, 2));
        indexes.add(List.of(1, 3));
        indexes.add(List.of(2, 3));
        indexes.add(List.of(0, 1, 2));
        indexes.add(List.of(0, 1, 3));
        indexes.add(List.of(0, 2, 3));
        indexes.add(List.of(1, 2, 3));
        indexes.add(List.of(0, 1, 2, 3));

        int sum = 0;
        for (List<Integer> index : indexes) {
            int size = CombinationFilter.neighbouringConsecutiveNumberPairs(combinations, index).size();
            sum += size;
        }
        assertEquals(ALL_COMBINATIONS_COUNT, sum);
    }

    @Test
    void whenFilteringForNoNeighbouringConsecutiveNumberPairsItShouldHave3Results() {
        List<List<GameNumber>> results = CombinationFilter.neighbouringConsecutiveNumberPairs(customCombinations, List.of());

        assertEquals(3, results.size());
        assertEquals(8, results.get(0).get(4).getValue());
        assertEquals(9, results.get(1).get(4).getValue());
        assertEquals(9, results.get(2).get(4).getValue());
    }

    @Test
    void whenFilteringForNeighbouringConsecutiveNumberPairsAt3rdAnd4thPlacesItShouldHave1Result() {
        List<List<GameNumber>> results = CombinationFilter.neighbouringConsecutiveNumberPairs(customCombinations, List.of(2, 3));

        assertEquals(1, results.size());
        assertEquals(results.get(0).get(2).getValue() + 1, results.get(0).get(3).getValue());
        assertEquals(results.get(0).get(3).getValue() + 1, results.get(0).get(4).getValue());
    }
}
