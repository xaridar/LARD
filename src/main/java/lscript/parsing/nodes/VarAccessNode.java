package lscript.parsing.nodes;

import lscript.lexing.Token;

/**
 * A simple Node representing an attempt to access a variable.
 */
public class VarAccessNode extends Node {

    private final Token token;

    /**
     * @param token - The Token containing the variable name to be accessed.
     */
    public VarAccessNode(Token token) {
        super(token.getPosStart(), token.getPosEnd());
        this.token = token;
    }

    /**
     * @return a Token representing a variable name.
     */
    public Token getToken() {
        return token;
    }
}
