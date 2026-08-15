package servicehub.ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapAndUnionFindTest {

    @Test
    void mapPutAndGet() {
        Map<String, Integer> map = new Map<>();
        map.put("Balme", 100);
        map.put("CSDept", 200);
        assertEquals(100, map.get("Balme"));
        assertEquals(200, map.get("CSDept"));
        assertEquals(2, map.size());
    }

    @Test
    void mapUpdateAndContains() {
        Map<String, String> map = new Map<>();
        map.put("L001", "A");
        map.put("L001", "B");
        assertEquals("B", map.get("L001"));
        assertEquals(1, map.size());
        assertTrue(map.containsKey("L001"));
        assertFalse(map.containsKey("L999"));
        assertNull(map.get("L999"));
    }

    @Test
    void mapRemove() {
        Map<String, Integer> map = new Map<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        assertEquals(2, map.remove("b"));
        assertFalse(map.containsKey("b"));
        assertEquals(2, map.size());
        assertNull(map.remove("zzz"));
    }

    @Test
    void mapMinAndEmpty() {
        Map<Integer, String> map = new Map<>();
        assertTrue(map.isEmpty());
        map.put(30, "x");
        map.put(10, "y");
        assertEquals(10, map.findMin());
        map.clear();
        assertTrue(map.isEmpty());
        assertThrows(IllegalStateException.class, map::findMin);
    }

    @Test
    void unionFindFindRoots() {
        UnionFind uf = new UnionFind(6);
        for (int i = 0; i < 6; i++) assertEquals(i, uf.find(i));
        uf.union(0, 1);
        uf.union(2, 3);
        assertTrue(uf.connected(0, 1));
        assertTrue(uf.connected(2, 3));
        assertFalse(uf.connected(0, 2));
        assertEquals(4, uf.count());
    }

    @Test
    void unionFindPathCompressionAndRank() {
        UnionFind uf = new UnionFind(10);
        for (int i = 1; i < 10; i++) uf.union(0, i);
        for (int i = 0; i < 10; i++) assertEquals(uf.find(0), uf.find(i));
        assertTrue(uf.connected(0, 9));
        assertEquals(1, uf.count());
    }

    @Test
    void unionFindOutOfBoundsThrows() {
        UnionFind uf = new UnionFind(3);
        assertThrows(IndexOutOfBoundsException.class, () -> uf.find(10));
        assertThrows(IllegalArgumentException.class, () -> new UnionFind(-1));
    }

    @Test
    void genericDisjointSetOnStrings() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("Balme");
        ds.makeSet("CS");
        ds.makeSet("UGMC");
        assertFalse(ds.connected("Balme", "CS"));
        ds.union("Balme", "CS");
        assertTrue(ds.connected("Balme", "CS"));
        assertFalse(ds.connected("Balme", "UGMC"));
        assertEquals(3, ds.size());
    }

    @Test
    void genericDisjointSetTransitiveConnectivity() {
        DisjointSet<String> ds = new DisjointSet<>();
        for (String v : new String[]{"A", "B", "C", "D", "E"}) ds.makeSet(v);
        ds.union("A", "B");
        ds.union("B", "C");
        assertTrue(ds.connected("A", "C"));
        ds.union("D", "E");
        assertFalse(ds.connected("A", "D"));
        ds.union("C", "D");
        assertTrue(ds.connected("A", "E"));
    }

    @Test
    void unknownElementThrows() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("known");
        assertThrows(IllegalArgumentException.class, () -> ds.find("unknown"));
    }
}
