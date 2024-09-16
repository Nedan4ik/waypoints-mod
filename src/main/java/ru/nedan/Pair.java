package ru.nedan;

public class Pair<V, Z> {
    private final V first;
    private final Z second;

    public Pair(V first, Z second) {
        this.first = first;
        this.second = second;
    }

    public V getFirst() {
        return first;
    }

    public Z getSecond() {
        return second;
    }
}
