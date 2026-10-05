package edu.drexel.se320;

public class BinarySearch {

    // DO NOT MODIFY THIS SIGNATURE
    // This includes the protected modifier; the autograder currently relies
    // on a combination of overloading and visibility hacks to swap out your
    // code at runtime to test your test suite.
    protected static <T extends Comparable<T>> int binarySearchImplementation(T[] array, T elem) {
        // TODO: Implement binary search here. The signature above sets you up to ensure the array elements have a compareTo method
        // The code below here is just so the project will compile out of the box; go ahead and delete these.
        if (array == null || array.length == 0 || elem == null) {
            throw new IllegalArgumentException("Array and element must not be null or empty");
        }
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = array[mid].compareTo(elem);

            if (comparison == 0) {
                return mid;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        throw new IllegalArgumentException("Element not found in the array");
    }
}
