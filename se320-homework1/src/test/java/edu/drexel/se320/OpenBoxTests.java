package edu.drexel.se320;

// Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.lessThan;

// Core JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class OpenBoxTests extends BinarySearchBase {
    @Test
    public void deleteThisOnceYouHaveYourOwnTests() {
        assertEquals(0, 0);
    }
    // covers entering the loop, finding a match, valid inputs, etc
    @Test 
    public void finds_middle_element_test() {
        Integer[] arr = { 1, 2, 3 };
        assertEquals(1, find(arr, 2));
    }
    // tests for binary search not finding element in array
    @Test 
    public void target_between_two_elements_test() {
        Integer[] arr = { 10, 20, 30};
        assertThrows(NoSuchElementException.class, () -> find(arr, 15));
    }
    @Test 
    public void null_array_test() {
        Integer[] arr = null;
        assertThrows(IllegalArgumentException.class, () -> find(arr, 0));
    }
    @Test 
    public void target_null_array_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertThrows(IllegalArgumentException.class, () -> find(arr, null));
    }
    @Test 
    public void rejects_empty_array_test() {
        Integer[] arr = {};
        assertThrows(IllegalArgumentException.class, () -> find(arr, 0));
    }
}

