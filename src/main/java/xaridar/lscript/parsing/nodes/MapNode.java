package lscript.parsing.nodes;

import lscript.Tuple;
import lscript.lexing.Position;

import java.util.List;

/**
 * A simple Node representing a map.
 */
public class MapNode extends Node {
    private final List<Tuple<Node, Node>> pairs;

    /**
     * @param pairs - a list of Tuples of Nodes contained in the map.
     * @param posStart - The start position of the map.
     * @param posEnd - The end position of the map.
     */
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
