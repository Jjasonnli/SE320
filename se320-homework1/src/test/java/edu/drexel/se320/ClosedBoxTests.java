package edu.drexel.se320;

// Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.lessThan;

// Core JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.beans.Transient;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

public class ClosedBoxTests extends BinarySearchBase {

    /**
     * This is just an example of how to call the generic find method which dispatches to your binary search. 
     * Recall that List<int> is not a permitted Java type, but List<Integer> is, and the
     * Java compiler automatically converts values between int and Integer when needed,
     * but generic types must use Integer (or Char, Double, etc. rather than other primitives).
     * For regular object types (e.g., String) there's nothing special to do, just use the class.
    */
    public void exampleCallToDelete() {
        // Java generics do not treat primitives the same as object types.
        // To pass a primitive type (int, double, etc.) to the find method, you need to actually use the corresponding "boxed" version (Integer, Double, etc.) which is a class-based version of each primitive.
        // For types which are already/always objects, like String, everything will just work.
        // This method is not part of a test, but just a refresher/crash-course for those who may not have tried passing primitives to generic Java methods previously.
        // You should not modify this method; no part of the assignment requires you to do so, and this
        // can simply be deleted.

        Integer[] arr = { 0, 1, 2 };
        find(arr, 1);

        String[] arr2 = { "a", "b", "c", "foo" };
        find(arr2, "c");

    }
    // all test cases uses integer values
    @Test 
    public void finds_first_element_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertEquals(0, find(arr, 0));
    }
    @Test 
    public void finds_last_element_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 12 };
        assertEquals(9, find(arr, 12));
    }
    @Test 
    public void finds_middle_element_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 100, 101, 102, 103, 104 };
        assertEquals(5, find(arr, 100));
    }
    @Test 
    public void only_one_element_test() {
        Integer[] arr = { 0 };
        assertEquals(0, find(arr, 0));
    }
    @Test 
    public void rejects_empty_array_test() {
        Integer[] arr = {};
        assertThrows(IllegalArgumentException.class, () -> find(arr, 0));
    }
    @Test 
    public void NoSuchElementException_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertThrows(NoSuchElementException.class, () -> find(arr, 10));
    }
    @Test
    public void finds_both_elements_in_two_element_array_test() {
        Integer[] arr = {10, 20};

        assertEquals(0, find(arr, 10));
        assertEquals(1, find(arr, 20));
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
    public void target_higher_than_max_array_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertThrows(NoSuchElementException.class, () -> find(arr, 10));
    }
    @Test
    public void target_lower_than_min_array_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertThrows(NoSuchElementException.class, () -> find(arr, -1));
    }
    @Test 
    public void negative_numbers_test() {
        Integer[] arr = { -10, -9, -8, -7, -6, -5, -4, -3, -2, -1 };
        assertEquals(0, find(arr, -10));
        assertEquals(9, find(arr, -1));
        assertEquals(5, find(arr, -5));
    }
    @Test 
    public void negative_and_positive_numbers_and_zero_test() {
        Integer[] arr = { -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
        assertEquals(0, find(arr, -10));
        assertEquals(15, find(arr, 5));
        assertEquals(10, find(arr, 0));
    }
    @Test 
    public void duplicate_numbers_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 5, 5, 6, 7, 8, 9 };
        int index = find(arr, 5);
        assertTrue(index == 5 || index == 6 || index == 7);
    }
    @Test 
    public void target_between_two_elements_test() {
        Integer[] arr = { 10, 20, 30};
        assertThrows(NoSuchElementException.class, () -> find(arr, 15));
    }
    @Test 
    public void all_elements_same_test() {
        Integer[] arr = { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
        int index = find(arr, 5);
        assertTrue(index >= 0 && index <= 9);
    }
    @Test 
    public void successful_search_does_not_modify_array_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Integer[] arrCopy = arr.clone();
        find(arr, 5);
        assertArrayEquals(arrCopy, arr);
    }
    @Test 
    public void unsuccessful_search_does_not_modify_array_test() {
        Integer[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Integer[] arrCopy = arr.clone();
        assertThrows(NoSuchElementException.class, () -> find(arr, 10));
        assertArrayEquals(arrCopy, arr);
    }
    @Test
    public void unsorted_array_test() {
        Integer[] arr = {0, 1, 2, 3, 4, 100, 6, 7, 8, 9};
        Integer[] arrCopy = arr.clone();

        // Unsorted input may return a matching index or report no match
        try {
            int index = find(arr, 100);

            assertTrue(index >= 0 && index < arr.length);
            assertEquals(Integer.valueOf(100), arr[index]);
        } catch (NoSuchElementException exception) {
            assertTrue(exception.getMessage() != null
                && !exception.getMessage().isBlank());
        }

        assertArrayEquals(arrCopy, arr);
    }
}

