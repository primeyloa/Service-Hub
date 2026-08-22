package servicehub.algorithms;

public class Search {
    public static <T extends Comparable <T>> int linearSearch (T[] array, T target) {
        for(int i=0; i < array.length; i++){
            if (array[i].compareTo(target)==0) {
                return i;
            }
        }
        return -1;
    }
 
    public static <T extends Comparable <T>> int binarySearch (T[] array, T target) {
        int low = 0;
        int high = array.length - 1;

        while(low <= high){
            int mid = (low + high) / 2;
            int compare = target.compareTo(array[mid]);

            if (compare < 0 ) high = mid -1;
            else if (compare > 0) low = mid +1;
            else return mid;
        }
        return -1;
    }
}

