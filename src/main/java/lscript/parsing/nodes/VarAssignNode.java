package lscript.parsing.nodes;

import lscript.lexing.Token;

public class VarAssignNode extends Node {
    private final String type;
    private final Token token;
    private final Node valueNode;

    public VarAssignNode(String type, Token token, Node valueNode) {
        super(token.getPosStart(), token.getPosEnd());
        this.type = type;
        this.token = token;
        this.valueNode = valueNode;
    }

    public Token getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Node getValueNode() {
        return valueNode;
    }
}
