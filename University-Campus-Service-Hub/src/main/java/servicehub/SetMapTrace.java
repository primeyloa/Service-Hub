package servicehub;

import ds.MySet;
import ds.MyMap;

public class SetMapTrace {

    public static void main(String[] args) {

        MySet<Integer> set = new MySet<>();

        set.add(10);
        set.add(20);
        set.add(10);

        System.out.println("Set size: " + set.size());
        System.out.println("Contains 20: " + set.contains(20));

        set.remove(20);
        System.out.println("Contains 20 after removal: " + set.contains(20));

        MyMap<String, Integer> map = new MyMap<>();

        map.put("Ama", 10);
        map.put("Kojo", 20);

        System.out.println("Ama -> " + map.get("Ama"));
        System.out.println("Map size: " + map.size());

        map.remove("Kojo");
        System.out.println("Contains Kojo: " + map.containsKey("Kojo"));
    }
}