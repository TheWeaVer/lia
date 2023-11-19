package com.lia.hephaistus.chord.theweaver;

public interface Node {
    /**
     * Add new {@link Node} in finger table.
     */
    Node join(Node newNode);

    /**
     * Withdraw existed node from the chord.
     */
    void withdraw(Node node);

    /**
     * Find key's successor {@link Node}.
     */
    Node findSuccessor(String key);

    /**
     * Find key's predecessor {@link Node}.
     */
    Node findPredecessor(String key);

    /**
     * Find the closest finger {@link Node} of {@param key}.
     */
    Node closestPrecedingFinger(String key);
}
