package lscript.parsing.nodes;

import lscript.Tuple;
import lscript.lexing.Position;

import java.util.List;

/**
 * A simple Node representing a map.
 */
public class MapNode extends Node {
    private final List<Tuple<Node, Node>> pairs;

    public MapNode(List<Tuple<Node, Node>> pairs, Position posStart, Position posEnd) {
        super(posStart, posEnd);
        this.pairs = pairs;
    }

    /**
     * @return a List of Tuples containing all of the elements in the map.
     */
    public List<Tuple<Node, Node>> getPairs() {
        return pairs;
    }
}
