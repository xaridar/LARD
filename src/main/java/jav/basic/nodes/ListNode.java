package jav.basic.nodes;

import jav.basic.Position;

import java.util.List;

public class ListNode extends Node {

    private final List<Node> nodes;

    public ListNode(List<Node> nodes, Position posStart, Position posEnd) {
        super(posStart, posEnd);
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
