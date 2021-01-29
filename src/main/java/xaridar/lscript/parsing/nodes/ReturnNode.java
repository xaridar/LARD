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
 * A simple Node representing a 'return' statement.
 */
public class ReturnNode extends Node {
    private final List<Node> nodesToCall;

    /**
     * @param nodesToCall - A list of Nodes called in a return statement.
     * @param posStart - the start Position of this Node.
     * @param posEnd - the end position of this Node.
     */
    public ReturnNode(List<Node> nodesToCall, Position posStart, Position posEnd) {
        super(posStart, posEnd);
        this.nodesToCall = nodesToCall;
    }

    /**
     * @return a stored list of nodes returned by this return statement.
     */
    public List<Node> getNodesToCall() {
        return nodesToCall;
    }

    /**
     * @param node - a Node to addto this return statement.
     */
    public void addNodeToCall(Node node) {
        nodesToCall.add(node);
    }
}
