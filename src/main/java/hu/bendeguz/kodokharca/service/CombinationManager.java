package hu.bendeguz.kodokharca.service;

import java.util.*;


public class CombinationManager {

    private int[] currentCombination = new int[0];
    private final boolean[] value;
    private final int totalElementCount;
    private final int elementsInArray;


    public CombinationManager(int totalElementCount, int elementsInArray) {
        if (totalElementCount < 0 || elementsInArray < 0) {
            throw new IllegalArgumentException("The number of elements or combinations can not be less than 0!");
        }
        if (totalElementCount < elementsInArray) {
            throw new IllegalArgumentException("The total number of elements can not be smaller than the number of combinations!");
        }

        this.totalElementCount = totalElementCount;
        this.elementsInArray = elementsInArray;
        value = new boolean[totalElementCount];
        Arrays.fill(value, true);
    }

    public CombinationManager(int totalElementCount, int elementsInArray, int[] currentCombination) {
        this(totalElementCount, elementsInArray);
        this.currentCombination = currentCombination;
    }

    public int[] nextCombination() {
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

    private int incrementNumber(int x) {
        for (int i = x + 1; i < value.length; i++) {
            if(value[i]) {
                value[i] = false;
                return i;
            }
        }

        //when no bigger number left
        return -1;
    }

    private void initFirstCombination() {
        currentCombination = new int[elementsInArray];
        for (int i = 0; i < currentCombination.length; i++) {
            value[i] = false;
            currentCombination[i] = i;
        }
    }

    public int[] getCurrentCombination() {
        return currentCombination.clone();
    }

    public int getTotalElementCount() {
        return totalElementCount;
    }

    public int getElementsInArray() {
        return elementsInArray;
    }

}
