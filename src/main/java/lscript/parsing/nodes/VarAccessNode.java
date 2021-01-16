package lscript.parsing.nodes;

import lscript.lexing.Token;

/**
 * A simple Node representing an attempt to access a variable.
 */
public class VarAccessNode extends Node {

    private final Token token;

    public VarAccessNode(Token token) {
        super(token.getPosStart(), token.getPosEnd());
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
