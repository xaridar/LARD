package jav.basic.nodes;

import jav.basic.Position;
import jav.basic.Token;

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

    @Override
    public String toString() {
        return "(" + operationToken + ", " + node + ")";
    }
}
