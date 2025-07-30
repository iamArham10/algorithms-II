import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexedMaxPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int size;
    private int maxSize;
    private Key[] keys; // stores the priority of i (comparable value)
    private int[] pq; // stores index of the key at heap position index i
    private int[] qp; // index i of qp stores heap position of key i

    public IndexedMaxPQ(int maxSize) {
        if (maxSize < 0) throw new IllegalArgumentException();
        this.keys = (Key[]) new Comparable[maxSize + 1];
        this.pq = new int[maxSize + 1];
        this.qp = new int[maxSize + 1];
        Arrays.fill(qp, -1);
        this.maxSize = maxSize;
        this.size = 0;
    }

    public void insert(int i, Key key) {
        validateIndex(i);
        // there's already a key
        if (contains(i)) {
            throw new IllegalArgumentException("Index is already associated with a key");
        }

        // adding key to queue
        ++size;
        keys[i] = key;
        pq[size] = i;
        qp[i] = size;
        swim(size);
    }

    public void delete(int i) {
        validateIndex(i);

        if (qp[i] == -1) {
            throw new IllegalArgumentException("Key not present");
        }

        // exchange with last element and delete the node
        int iHeapPosition = qp[i];
        exchange(iHeapPosition, size--);

        swim(iHeapPosition);
        sink(iHeapPosition);

        keys[i] = null;
        qp[i] = -1;
        pq[size + 1] = -1;
    }

    public void decreaseKey(int i, Key key) {
        validateIndex(i);
        if (!contains(i)) {
            throw new IllegalArgumentException("index is not in the priority queue");
        }
        if (keys[i].compareTo(key) <= 0) {
            throw new IllegalArgumentException("new is not smaller than current key");
        }

        keys[i] = key;
        sink(qp[i]);
    }

    public int delMax() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue is empty");

        int maxIndex = pq[1]; // index of max key
        exchange(1, size); // key with last node key
        size--;

        sink(1); // restore heap property

        qp[maxIndex] = -1; // mark index of maximum key as deleted
        pq[size + 1] = -1; // delete from pq
        keys[maxIndex] = null; // remove key

        return maxIndex;
    }

    public void increaseKey(int i, Key key) {
        validateIndex(i);
        if (!contains(i)) {
            throw new NoSuchElementException("index is not in the priority queue");
        }
        if (keys[i].compareTo(key) >= 0) {
            throw new IllegalArgumentException(
                    "key associated with index is already greater than provided");
        }

        keys[i] = key;
        swim(qp[i]);
    }

    public boolean contains(int i) {
        validateIndex(i);
        return qp[i] != -1;
    }

    public Key keyOf(int i) {
        validateIndex(i);
        if (!contains(i)) throw new IllegalArgumentException("index is not in the priority queue");
        return keys[i];
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int maxIndex() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue empty");
        return pq[1];
    }

    public Key maxKey() {
        return keys[maxIndex()];
    }

    public int size() {
        return this.size;
    }

    public void changeKey(int i, Key key) {
        validateIndex(i);
        if (!contains(i)) throw new NoSuchElementException("Index not in priority queue");
        keys[i] = key;
        swim(qp[i]);
        sink(qp[i]);
    }

    private void exchange(int position1, int position2) {
        // exchange heap position
        int temp = pq[position1];
        pq[position1] = pq[position2];
        pq[position2] = temp;

        // assign new heap positions of index
        qp[pq[position1]] = position1;
        qp[pq[position2]] = position2;
    }

    private void swim(int nodePosition) {
        while (nodePosition > 1
                && keys[pq[nodePosition / 2]].compareTo(keys[pq[nodePosition]]) < 0) {
            exchange(nodePosition, nodePosition / 2);
            nodePosition = nodePosition / 2;
        }
    }

    private void sink(int nodePosition) {
        while (nodePosition * 2 <= this.size) {
            int child = nodePosition * 2;
            // if right child exists and is greater than left
            if (child < size && keys[pq[child]].compareTo(keys[pq[child + 1]]) < 0) {
                ++child;
            }

            // break if heap position is satisfied
            if (keys[pq[nodePosition]].compareTo(keys[pq[child]]) >= 0) {
                break;
            }

            exchange(nodePosition, child);
            nodePosition = child;
        }
    }

    private void validateIndex(int i) {
        if (i < 0 || i >= this.maxSize) {
            throw new IllegalArgumentException("Index out of range");
        }
    }

    public Iterator<Integer> iterator() {
        return new PqIterator();
    }

    private class PqIterator implements Iterator<Integer> {
        private IndexedMaxPQ<Key> clone;

        public PqIterator() {
            clone = new IndexedMaxPQ<Key>(pq.length - 1);
            for (int i = 1; i <= size; i++) {
                clone.insert(pq[i], keys[pq[i]]);
            }
        }

        public boolean hasNext() {
            return !clone.isEmpty();
        }

        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return clone.delMax();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== IndexedMaxPQ Simple Test ===\n");

        // Create priority queue
        IndexedMaxPQ<Integer> pq = new IndexedMaxPQ<>(10);
        System.out.println("Created empty priority queue");
        System.out.println("Is empty: " + pq.isEmpty());
        System.out.println("Size: " + pq.size());

        // Insert elements
        pq.insert(3, 30);
        pq.insert(1, 10);
        pq.insert(4, 40);
        pq.insert(2, 20);
        pq.insert(0, 50);

        System.out.println("\nAfter inserting (3,30), (1,10), (4,40), (2,20), (0,50):");
        System.out.println("Size: " + pq.size());
        System.out.println("Max index: " + pq.maxIndex());
        System.out.println("Max key: " + pq.maxKey());
        System.out.println("Contains index 3: " + pq.contains(3));
        System.out.println("Key of index 1: " + pq.keyOf(1));

        // Modify keys
        pq.increaseKey(1, 60);
        System.out.println("\nAfter increasing key at index 1 to 60:");
        System.out.println("New max index: " + pq.maxIndex());
        System.out.println("New max key: " + pq.maxKey());

        pq.decreaseKey(0, 25);
        System.out.println("\nAfter decreasing key at index 0 to 25:");
        System.out.println("Max index: " + pq.maxIndex());
        System.out.println("Max key: " + pq.maxKey());

        // Delete operations
        int deletedMax = pq.delMax();
        System.out.println("\nDeleted max element at index: " + deletedMax);
        System.out.println("New max index: " + pq.maxIndex());
        System.out.println("New size: " + pq.size());

        pq.delete(2);
        System.out.println("\nAfter deleting index 2:");
        System.out.println("Size: " + pq.size());
        System.out.println("Contains index 2: " + pq.contains(2));

        // Test iterator
        System.out.println("\nIterating through elements (in priority order):");
        for (Integer index : pq) {
            System.out.println("Index: " + index + ", Key: " + pq.keyOf(index));
        }

        // Extract all remaining elements
        System.out.println("\nExtracting all remaining elements:");
        while (!pq.isEmpty()) {
            int maxIndex = pq.delMax();
            System.out.println("Extracted index " + maxIndex + " (size now: " + pq.size() + ")");
        }

        System.out.println("\nFinal state - Empty: " + pq.isEmpty());

    }
}
