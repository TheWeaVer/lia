package com.lia.hephaistus.chord.theweaver;

public interface ChordNode {
    /**
     * Add new {@link ChordNode} to cluster.
     * The cluster tries to find proper place for the new {@link ChordNode}.
     */
    ChordNode join(ChordNode newChordNode);

    /**
     * Withdraw existed node from the cluster.
     * The cluster tries to rebalance nodeIds and records to proper node.
     */
    void withdraw(ChordNode chordNode);

    /**
     * Save record in node. The node finds proper node to save.
     * @throws IllegalArgumentException If the key already exist.
     */
    void save(int key, String value);

    /**
     * Delete record in node.
     */
    void delete(int key);

    /**
     * Get record in node.
     */
    String get(int key);
}
