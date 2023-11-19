package com.lia.hephaistus.chord.theweaver;

public class DefaultNode implements Node {
    private String key;
    private Node predecessor;
    private Node successor;
    private FingerTable fingerTable;

    @Override
    public Node join(Node newNode) {
        return null;
    }

    @Override
    public void withdraw(Node node) {

    }

    @Override
    public Node findSuccessor(String key) {
        return null;
    }

    @Override
    public Node findPredecessor(String key) {
        return null;
    }

    @Override
    public Node closestPrecedingFinger(String key) {
        return null;
    }
}
