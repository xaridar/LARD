package jav.basic.nodes;

import jav.basic.Token;

public class NumberNode extends Node {
    private final Token token;

    public NumberNode(Token token) {
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
