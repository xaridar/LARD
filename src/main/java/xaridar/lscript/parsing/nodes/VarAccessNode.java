package lscript.parsing.nodes;

import lscript.lexing.Token;

/**
 * A simple Node representing an attempt to access a variable.
 */
public class VarAccessNode extends Node {

    private final Token context;
    private final Token token;

    /**
     * @param context - A String representing the Context name to access the variable from (separated by '.').
     * @param token - The Token containing the variable name to be accessed.
     */
    public VarAccessNode(Token context, Token token) {
        super(context != null ? context.getPosStart().copy() : token.getPosStart().copy(), token.getPosEnd().copy());
        this.context = context;
        this.token = token;
    }
    /**
     * Overloaded constructor without a context String.
     * @param token - The Token containing the variable name to be accessed.
     */
    public VarAccessNode(Token token) {
        this(null, token);
    }

    /**
     * @return a Token representing a variable name.
     */
    public Token getToken() {
        return token;
    }

    /**
     * @return A String representing the Context name to access the variable from (separated by '.').
     */
    public Token getContext() {
        return context;
    }
}
