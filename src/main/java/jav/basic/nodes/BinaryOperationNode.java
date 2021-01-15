package jav.basic.nodes;

import jav.basic.Position;
import jav.basic.Token;

public class BinaryOperationNode extends Node {
    private final Node startNode;
    private final Token operationToken;
    private final Node endNode;

    public BinaryOperationNode(Node startNode, Token operationToken, Node endNode) {
        super(startNode.getPosStart(), endNode.getPosEnd());
        this.startNode = startNode;
        this.operationToken = operationToken;
        this.endNode = endNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Token getOperationToken() {
        return operationToken;
    }

    @Override
    public String toString() {
        return "(" + startNode + ", " + operationToken + ", " + endNode + ")";
    }
}
