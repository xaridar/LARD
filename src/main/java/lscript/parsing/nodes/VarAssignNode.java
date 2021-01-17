package lscript.parsing.nodes;

import lscript.lexing.Token;

/**
 * A simple Node representing an attempt to assign a value to a variable.
 */
public class VarAssignNode extends Node {
    private final String type;
    private final Token token;
    private final Node valueNode;

    /**
     * @param type - A String representing the variable's type.
     * @param token - The Token containing the variable name to be accessed.
     * @param valueNode - A Node containing the value to be assigned.
     */
    public VarAssignNode(String type, Token token, Node valueNode) {
        super(token.getPosStart(), token.getPosEnd());
        this.type = type;
        this.token = token;
        this.valueNode = valueNode;
    }

    /**
     * @return a Token representing a variable name.
     */
    public Token getToken() {
        return token;
    }

    /**
     * @return a String representing the type of the variable.
     */
    public String getType() {
        return type;
    }

    /**
     * @return a Node containing the value to set to the variable.
     */
    public Node getValueNode() {
        return valueNode;
    }
}
