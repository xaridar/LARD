package jav.basic.nodes;

import jav.Tuple;
import jav.basic.Position;

import java.util.List;

public class MapNode extends Node {
    private final List<Tuple<Node, Node>> pairs;

    public MapNode(List<Tuple<Node, Node>> pairs, Position posStart, Position posEnd) {
        super(posStart, posEnd);
        this.pairs = pairs;
    }

    public List<Tuple<Node, Node>> getPairs() {
        return pairs;
    }
}
