package lscript.parsing.nodes;

import lscript.lexing.Token;

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
