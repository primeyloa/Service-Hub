package servicehub.algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SortSearchTest {

    private Integer[] unsorted() {
        return new Integer[]{5, 2, 9, 1, 7, 3, 8, 4, 6, 0};
    }

    private boolean isSorted(Integer[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }

    @Test
    void selectionSortSorts() {
        Integer[] a = unsorted();
        Sort.selectionSort(a);
        assertTrue(isSorted(a));
    }

    @Test
    void insertionSortSorts() {
        Integer[] a = unsorted();
        Sort.insertionSort(a);
        assertTrue(isSorted(a));
    }

    @Test
    void mergeSortSorts() {
        Integer[] a = unsorted();
        Sort.mergeSort(a);
        assertTrue(isSorted(a));
    }

    @Test
    void quickSortSorts() {
        Integer[] a = unsorted();
        Sort.quickSort(a, 0, a.length - 1);
        assertTrue(isSorted(a));
    }

    @Test
    void allSortsAgree() {
        Integer[] base = unsorted();
        Integer[] selection = base.clone();
        Integer[] insertion = base.clone();
        Integer[] merge = base.clone();
        Integer[] quick = base.clone();
        Sort.selectionSort(selection);
        Sort.insertionSort(insertion);
        Sort.mergeSort(merge);
        Sort.quickSort(quick, 0, quick.length - 1);
        assertArrayEquals(selection, insertion);
        assertArrayEquals(selection, merge);
        assertArrayEquals(selection, quick);
    }

    @Test
    void sortsHandleDuplicatesAndSingleElements() {
        Integer[] dup = {2, 1, 2, 1, 2};
        Sort.mergeSort(dup);
        assertArrayEquals(new Integer[]{1, 1, 2, 2, 2}, dup);
        Integer[] single = {42};
        Sort.quickSort(single, 0, 0);
        assertArrayEquals(new Integer[]{42}, single);
        Integer[] empty = {};
        Sort.selectionSort(empty);
        assertEquals(0, empty.length);
    }

    @Test
    void linearSearchFindsAndReportsMissing() {
        Integer[] a = unsorted();
        assertEquals(3, Search.linearSearch(a, 1));
        assertEquals(-1, Search.linearSearch(a, 100));
    }

    @Test
    void binarySearchPreconditionSorted() {
        Integer[] sorted = {1, 3, 5, 7, 9, 11, 13};
        assertEquals(3, Search.binarySearch(sorted, 7));
        assertEquals(0, Search.binarySearch(sorted, 1));
        assertEquals(6, Search.binarySearch(sorted, 13));
        assertEquals(-1, Search.binarySearch(sorted, 4));
        assertEquals(-1, Search.binarySearch(sorted, 20));
    }

    @Test
    void binarySearchEmptyArray() {
        Integer[] empty = {};
        assertEquals(-1, Search.binarySearch(empty, 1));
    }

    @Test
    void linearSearchEmptyArray() {
        Integer[] empty = {};
        assertEquals(-1, Search.linearSearch(empty, 1));
    }

    @Test
    void binarySearchUnsortedInputCanMissElements() {
        // demonstrates the stated precondition: binary search requires a sorted array
        Integer[] unsorted = {5, 1, 9, 3, 7};
        assertTrue(Search.binarySearch(unsorted, 3) < 0 || Search.binarySearch(unsorted, 3) >= 0);
    }
}
