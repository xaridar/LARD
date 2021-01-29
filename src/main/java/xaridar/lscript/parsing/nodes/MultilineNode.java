package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.lexing.Position;

import java.util.List;

/**
 * A node that represents multiline statements.
 */
public class MultilineNode extends Node {
    private final List<Node> nodes;

    /**
     * @param nodes - a list of Nodes contained in the Node.
     * @param posStart - The start position of the Node.
     * @param posEnd - The end position of the Node.
     */
    public MultilineNode(List<Node> nodes, Position posStart, Position posEnd) {
        super(posStart, posEnd);
        this.nodes = nodes;
    }

    /**
     * @return a List of Nodes contained in the list.
     */
    public List<Node> getNodes() {
        return nodes;
    }
}
