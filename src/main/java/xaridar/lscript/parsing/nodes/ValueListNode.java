package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import java.util.List;

/**
 * Represents a list of values, separated by commas.
 */
public class ValueListNode extends Node {

    private final List<Node> nodes;

    /**
     * @param nodes - A List of Nodes represented by this Node.
     */
    public ValueListNode(List<Node> nodes) {
        super(nodes.get(0).getPosStart(), nodes.get(nodes.size() - 1).getPosEnd());
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
