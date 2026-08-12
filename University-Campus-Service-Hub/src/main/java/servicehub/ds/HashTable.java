package servicehub.ds;

public class HashTable {

    // Team's required hash table size
    private static final int DEFAULT_SIZE = 919;

    private HashNode[] table;
    private int size;

    // Statistics
    private int elementCount;
    private int collisionCount;

    // Default constructor
    public HashTable() {
        this(DEFAULT_SIZE);
    }

    // Custom constructor
    public HashTable(int size) {
        this.size = size;
        table = new HashNode[size];
        elementCount = 0;
        collisionCount = 0;
    }

    // Hash Function
    private int hash(int requestID) {
        return Math.abs(requestID) % size;
    }

    // Insert
    public void insert(int requestID) {

        int index = hash(requestID);

        HashNode newNode = new HashNode(requestID);

        if (table[index] == null) {
            table[index] = newNode;
            elementCount++;
            return;
        }

        // Collision detected
        collisionCount++;

        HashNode current = table[index];

        while (current.next != null) {

            if (current.requestID == requestID)
                return; // Duplicate not allowed

            current = current.next;
        }

        if (current.requestID == requestID)
            return;

        current.next = newNode;
        elementCount++;
    }

    // Search
    public boolean search(int requestID) {

        int index = hash(requestID);

        HashNode current = table[index];

        while (current != null) {

            if (current.requestID == requestID)
                return true;

            current = current.next;
        }

        return false;
    }

    // Contains (Wrapper for search)
    public boolean contains(int requestID) {
        return search(requestID);
    }

    // Delete
    public void delete(int requestID) {

        int index = hash(requestID);

        HashNode current = table[index];
        HashNode previous = null;

        while (current != null) {

            if (current.requestID == requestID) {

                if (previous == null)
                    table[index] = current.next;
                else
                    previous.next = current.next;

                elementCount--;
                return;
            }

            previous = current;
            current = current.next;
        }
    }

    // Display Hash Table
    public void display() {

        System.out.println("\n        HASH TABLE        ");

        for (int i = 0; i < size; i++) {

            System.out.print("Bucket " + i + " -> ");

            HashNode current = table[i];

            while (current != null) {
                System.out.print(current + " -> ");
                current = current.next;
            }

            System.out.println("NULL");
        }

        System.out.println("   ");
    }

    // Number of stored elements
    public int size() {
        return elementCount;
    }

    // Current load factor
    public double getLoadFactor() {
        return (double) elementCount / size;
    }

    // Number of collisions encountered
    public int getCollisionCount() {
        return collisionCount;
    }

    // Clear the table
    public void clear() {
        table = new HashNode[size];
        elementCount = 0;
        collisionCount = 0;
    }
}