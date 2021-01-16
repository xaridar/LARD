package lscript.parsing.nodes;

import lscript.lexing.Token;

/**
 * A simple Node representing a unary operation.
 */
public class UnaryOperationNode extends Node {
    private final Token operationToken;
    private final Node node;

    public UnaryOperationNode(Token operationToken, Node node) {
        super(operationToken.getPosStart(), node.getPosEnd());
        this.operationToken = operationToken;
        this.node = node;
    }

    public Token getOperationToken() {
        return operationToken;
    }

    public Node getNode() {
        return node;
    }

    /**
     * @return a String representation of the Node for debugging.
     */
    @Override
    public String toString() {
        return "(" + operationToken + ", " + node + ")";
    }
}
