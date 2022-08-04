package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        int[] current = new int[5];
        int[] current2 = {-1, -1, -1, -1, -1};
        List<List<Integer>> list = new ArrayList<>();

        CombinationManager cm = new CombinationManager(20, 5);

        long selectionCount = 0;
        int double5Count = 0;


        for(current = cm.nextCombination(); current.length != 0; current = cm.nextCombination()) {
            selectionCount++;
            list.add(Arrays.stream(current).boxed().collect(Collectors.toList()));

            System.out.println(Arrays.toString(current));

            if (current.length - 2 == Arrays.stream(current).filter(value -> value != 5).filter(value -> value != 15).count()) {
                double5Count++;
            }
        }

        System.out.println("selectionCount = " + selectionCount);
        System.out.println("double5Count = " + double5Count);
    }
}
