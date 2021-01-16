package lscript.parsing.nodes;

import lscript.lexing.Token;

public class StringNode extends Node {
    private final Token token;

    public StringNode(Token token) {
        super(token.getPosStart(), token.getPosEnd());
        this.token = token;
    }

    @Override
    public String toString() {
        return token.toString();
    }

    public Token getToken() {
        return token;
    }
}
