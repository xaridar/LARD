package jav.basic.nodes;

import jav.basic.Position;
import jav.basic.Token;

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
