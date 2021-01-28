package xaridar.lscript.parsing.nodes;

import xaridar.lscript.lexing.Token;

/**
 * A simple Node representing a unary operation.
 */
public class UnaryOperationNode extends Node {
    private final Token operationToken;
    private final Node node;

    /**
     * @param operationToken - The Token representing the operation type of the binary operation.
     * @param node - The Node containing the target of the unary operation.
     */
    public UnaryOperationNode(Token operationToken, Node node) {
        super(operationToken.getPosStart(), node.getPosEnd());
        this.operationToken = operationToken;
        this.node = node;
    }

    /**
     * @return a Token representing the unary operation.
     */
    public Token getOperationToken() {
        return operationToken;
    }

    /**
     * @return a Node for the main body of the unary operation.
     */
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
