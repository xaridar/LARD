package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.lexing.Token;

/**
 * A simple Node representing either an integer or floating point number.
 */
public class NumberNode extends Node {
    private final Token token;

    /**
     * @param token - A Token representing the number value of the Node.
     */
    public NumberNode(Token token) {
        super(token.getPosStart(), token.getPosEnd());
        this.token = token;
    }

    /**
     * @return a String representation of the Node for debugging.
     */
    @Override
    public String toString() {
        return token.toString();
    }

    /**
     * @return the Token representing the number of this Node.
     */
    public Token getToken() {
        return token;
    }
}
