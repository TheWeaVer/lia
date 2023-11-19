package com.lia.hephaistus.chord.theweaver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The 128 size chord node(m = 7).
 * It does not generate any key, but throws exception if the given key is lager than 128.
 */
public class DefaultChordNode implements ChordNode {
    private final List<Integer> fingerTable;
    private final Map<Integer, String> records;
    private final int nodeId;
    private ChordNode predecessor;
    private ChordNode successor;

    public DefaultChordNode(int nodeId) {
        this.nodeId = nodeId;
        predecessor = this;
        successor = this;
        fingerTable = Arrays.asList(nodeId, nodeId, nodeId, nodeId, nodeId, nodeId, nodeId, nodeId);
        records = new HashMap<>();
    }

    @Override
    public ChordNode join(ChordNode newChordNode) {
        return null;
    }

    @Override
    public void withdraw(ChordNode chordNode) {

    }

    @Override
    public void save(int key, String value) {

    }

    @Override
    public void delete(int key) {

    }

    @Override
    public String get(int key) {
        return null;
    }

    private ChordNode findSuccessor(int nodeId) {
        return null;
    }

    private ChordNode findPredecessor(int nodeId) {
        return null;
    }

    private ChordNode closestPrecedingNode(int nodeId) {
        return null;
    }

    private void stabilize() {

    }
}
