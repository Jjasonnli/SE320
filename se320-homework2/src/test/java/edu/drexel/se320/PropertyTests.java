package edu.drexel.se320;

// Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.lessThan;

// Core JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

// Jqwik
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.Statistics;


public class PropertyTests extends BinarySearchBase {
    @Provide
    Arbitrary<Integer[]> sortedArrs() {
        return Arbitraries.integers()
                .between(0, 100)
                .array(Integer[].class)
                .ofMinSize(0)
                .ofMaxSize(30)
                .map(array -> {
                    Arrays.sort(array);
                    return array;
                });
    }
    // finds the element in a single element array
    @Property
    public boolean checkSingleArrayElt(@ForAll int x) {
	    Integer[] arr = { x };
	    return find(arr, x) == 0;
    }
    // finds the element in a single element array
    @Property
    public boolean one_element_array_test(
            @ForAll @Size(min = 1, max = 1) Integer[] array) {

        return find(array, array[0]) == 0;
    }
    @Property
    public boolean finds_first_value_test(
            @ForAll("sortedArrs") Integer[] array) {
        
        // assume that the array has at least two elements to verify we can find the first element
        Assume.that(array.length >= 2);

        Integer target = array[0];
        int index = find(array, target);

        return index >= 0
                && index < array.length
                && array[index].equals(target);
    }
    @Property
    public boolean finds_last_value_test(
            @ForAll("sortedArrs") Integer[] array) {
        
        // assume that the array has at least two elements to verify we can find the last element
        Assume.that(array.length >= 2);

        Integer target = array[array.length - 1];
        int index = find(array, target);

        return index >= 0
                && index < array.length
                && array[index].equals(target);
    }
    @Property 
    public boolean finds_middle_value_test(
            @ForAll("sortedArrs") Integer[] array) {
        
        // assume that the array has at least three elements
        Assume.that(array.length >= 3);

        Integer target = array[array.length / 2];
        int index = find(array, target);

        return index >= 0
                && index < array.length
                && array[index].equals(target);
    }
    // better one for all 'middle' values, not just the middle of the array
    @Property
    public boolean finds_random_value_test(
        @ForAll("sortedArrs") Integer[] array,
        @ForAll int position) {
        
        // needs to be at least 3 elements to avoid the first and last elements
        Assume.that(array.length >= 3);
        
        // excludes the first and last elements of the array
        int targetIndex = 1 + Math.floorMod(position, array.length - 2);
        Integer target = array[targetIndex];
        int index = find(array, target);

        return index >= 0
                && index < array.length
                && array[index].equals(target);
    }
    @Property 
    public boolean element_below_minimum_test(
        @ForAll("sortedArrs") Integer[] array,
        @ForAll int x) {
        
        // assume that the array is not empty
        Assume.that(array.length > 0);

        // make sure x is less than the minimum element in the array
        Assume.that(x < array[0]);

        assertThrows(NoSuchElementException.class, () -> find(array, x));
        return true;
    }
    @Property 
    public boolean element_above_maximum_test(
        @ForAll("sortedArrs") Integer[] array,
        @ForAll int x) {
        
        // assume that the array is not empty
        Assume.that(array.length > 0);

        // make sure x is greater than the maximum element in the array
        Assume.that(x > array[array.length - 1]);

        assertThrows(NoSuchElementException.class, () -> find(array, x));
        return true;
    }
    @Property 
    public boolean element_not_in_array_test(
        @ForAll("sortedArrs") Integer[] array,
        @ForAll int x) {
        
        // assume that the array is not empty
        Assume.that(array.length > 0);

        // make sure x is not in the array
        Assume.that(Arrays.binarySearch(array, x) < 0);

        assertThrows(NoSuchElementException.class, () -> find(array, x));
        return true;
    }
    @Property
    public boolean search_for_element_in_between_two_elements_test(
            @ForAll("sortedArrs") Integer[] array,
            @ForAll @IntRange(min = 0, max = 100) int x) {

        Assume.that(array.length >= 2);

        Assume.that(x > array[0]
                && x < array[array.length - 1]);

        Assume.that(!Arrays.asList(array).contains(x));

        assertThrows(NoSuchElementException.class,
                () -> find(array, x));

        return true;
    }
    @Property 
    public void null_array_test(@ForAll int target) {
        
        assertThrows(IllegalArgumentException.class,
                () -> find((Integer[]) null, target));
    }
    @Property
    public void empty_array_test(@ForAll int target) {
        assertThrows(IllegalArgumentException.class,
                () -> find(new Integer[0], target));
    }
    // last branch coverage null target
    @Property 
    public void target_null_test(@ForAll("sortedArrs") Integer[] array) {
        Assume.that(array.length > 0);

        assertThrows(IllegalArgumentException.class,
                () -> find(array, null));
    }
}

