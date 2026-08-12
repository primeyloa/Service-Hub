# Correctness Pack

A unified JUnit 5 test suite covering **normal / boundary / invalid-input**
tests for five structures: BST, Red-Black Tree, B-Tree, Hash Table, and a
Set/Map ADT.

67 tests, all passing, verified in this environment.

## The one thing you need to do

I don't have your actual `BST`, `RedBlackTree`, `BTree`, `HashTable`,
`MySet`/`MyMap` classes, so `src/main/java/ds/` currently contains
**reference implementations I wrote** that satisfy the API the tests
assume. They're correct (insert/delete stress-tested with thousands of
randomized operations) and everything currently passes against them.

**Swap them out**: replace the files in `src/main/java/ds/` with your
team's real implementations, keeping the same class names, package (`ds`),
and method signatures listed below. Then re-run. If a method name differs
(e.g. you called it `remove()` instead of `delete()`), either rename your
method or do a quick find-and-replace in the matching test file — that's
the only coupling.

If your classes live in a different package already, change the `package`
line at the top of each file in `src/main/java/ds/` to match, and update
the `import ds.*;` lines at the top of the test files accordingly.

## Assumed API (documented per-file too)

| Class | Methods |
|---|---|
| `BST<T extends Comparable<T>>` | `insert(T)`, `delete(T)`, `contains(T)`, `size()`, `isEmpty()`, `height()`, `inorder()` |
| `RedBlackTree<T extends Comparable<T>>` | same as BST, minus `height()` |
| `BTree<T extends Comparable<T>>` | constructor `BTree(int minimumDegree)`, `insert(T)`, `delete(T)`, `contains(T)`, `search(T)` (alias), `size()`, `isEmpty()`, `inorder()` |
| `HashTable<K,V>` | `put(K,V)`, `get(K)`, `remove(K)`, `containsKey(K)`, `size()`, `isEmpty()` |
| `MySet<T>` | `add(T)`, `remove(T)`, `contains(T)`, `size()`, `isEmpty()` |
| `MyMap<K,V>` | `put(K,V)`, `get(K)`, `remove(K)`, `containsKey(K)`, `size()`, `isEmpty()` |

## Assumed policies (flag these to your group leader if they should differ)

- `insert(null)` / `delete(null)` / `contains(null)` / `put(null, v)` / `get(null)` all throw `IllegalArgumentException`.
- Inserting a duplicate is a no-op (size doesn't change).
- Deleting/removing a value that isn't present is a safe no-op (no exception).
- `BTree` constructed with minimum degree `< 2` throws `IllegalArgumentException`.
- `HashTable` disallows null keys.

If your actual classes use `NullPointerException` instead of
`IllegalArgumentException`, or allow duplicates/nulls, just tell me and
I'll adjust the relevant assertions — don't want you re-writing 15 test
methods by hand for one policy difference.

## What "normal / boundary / invalid input" means here

Each structure's test file is split into three `@Nested` classes:

- **Normal operations** — typical insert/search/delete sequences, checking
  the structure produces the expected sorted order and correct membership.
- **Boundary conditions** — empty structure, single element, duplicates,
  deleting a missing key, extreme values (`Integer.MIN_VALUE`/`MAX_VALUE`),
  operations that force a rotation/split/merge/resize, and randomized
  stress sequences.
- **Invalid input handling** — `null` arguments and (for B-Tree) an invalid
  constructor argument.

## Why the Red-Black Tree and B-Tree tests are stronger than "does it still work"

`util/RBInvariantChecker.java` and `util/BTreeInvariantChecker.java` use
reflection to walk your actual tree's nodes and verify the structural
invariants directly (red-black coloring/black-height rules; B-tree
key-count bounds, sortedness, equal leaf depth) — not just "the values are
still there." This catches bugs where the values happen to still be
findable but the tree has silently lost its balance guarantees (e.g. a
missed rotation), which a black-box test alone wouldn't catch.

These checkers look for common field names (`root`, `left`, `right`,
`key`, `color`/`red`, `keys`, `children`, `leaf`, `t`). If your fields are
named differently, add your names to the candidate lists at the top of
each checker file — that's the only place field names are hardcoded.

## Running it

**With Maven** (once you have your real classes in `src/main/java/ds/`):
```
mvn test
```

**Without Maven**, using the bundled jars in `lib/`:
```
./run_tests.sh
```

**In an IDE**: right-click `CorrectnessPackSuite.java` and Run, or run any
individual `*CorrectnessTest.java` file on its own.

## Layout

```
correctness-pack/
├── pom.xml                          # Maven config (for your machine / IDE)
├── run_tests.sh                     # No-Maven fallback, uses lib/*.jar
├── lib/                              # Bundled JUnit 5 jars (for run_tests.sh)
├── src/main/java/ds/                # <-- replace these with your real classes
│   ├── BST.java
│   ├── RedBlackTree.java
│   ├── BTree.java
│   ├── HashTable.java
│   ├── MySet.java
│   └── MyMap.java
└── src/test/java/correctness/
    ├── BSTCorrectnessTest.java
    ├── RedBlackTreeCorrectnessTest.java
    ├── BTreeCorrectnessTest.java
    ├── HashTableCorrectnessTest.java
    ├── SetMapCorrectnessTest.java
    ├── CorrectnessPackSuite.java     # runs all five at once
    └── util/
        ├── RBInvariantChecker.java
        └── BTreeInvariantChecker.java
```

## Current test count

67 tests across 5 structures, all passing against the reference
implementations bundled here.
