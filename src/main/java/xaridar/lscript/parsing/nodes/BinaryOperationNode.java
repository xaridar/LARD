package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.lexing.Token;

/**
 * A simple Node representing a binary operation.
 */
public class BinaryOperationNode extends Node {
    private final Node startNode;
    private final Token operationToken;
    private final Node endNode;

    /**
     * @param startNode - The Node on the left of the binary operation.
     * @param operationToken - The Token representing the operation type of the binary operation.
     * @param endNode - The Node on the left of the binary operation.
     */
    public BinaryOperationNode(Node startNode, Token operationToken, Node endNode) {
        super(startNode.getPosStart(), endNode.getPosEnd());
        this.startNode = startNode;
        this.operationToken = operationToken;
        this.endNode = endNode;
    }

    /**
     * @return the Node on the right side of this binary operation.
     */
    public Node getEndNode() {
        return endNode;
    }

    /**
     * @return the Node on the left side of this binary operation.
     */
    public Node getStartNode() {
        return startNode;
    }

    /**
     * @return the Token in the middle of this binary operation.
     */
    public Token getOperationToken() {
        return operationToken;
    }

    /**
     * @return a String representation of the Node for debugging.
     */
    @Override
    public String toString() {
        return "(" + startNode + ", " + operationToken + ", " + endNode + ")";
    }
}
