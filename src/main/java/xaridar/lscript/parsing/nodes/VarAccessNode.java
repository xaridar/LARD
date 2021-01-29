package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.lexing.Token;

import java.util.Collections;
import java.util.List;

/**
 * A simple Node representing an attempt to access a variable.
 */
public class VarAccessNode extends Node {

    private final List<Token> nestedContexts;
    private final Token token;

    /**
     * @param nestedContexts - A List of nested Token names representing the Context name to access the variable from (separated by '.').
     * @param token - The Token containing the variable name to be accessed.
     */
    public VarAccessNode(List<Token> nestedContexts, Token token) {
        super(nestedContexts.size() != 0 ? nestedContexts.get(0).getPosStart().copy() : token.getPosStart().copy(), token.getPosEnd().copy());
        this.nestedContexts = nestedContexts;
        this.token = token;
    }
    /**
     * Overloaded constructor without a context String.
     * @param token - The Token containing the variable name to be accessed.
     */
    public VarAccessNode(Token token) {
        this(Collections.emptyList(), token);
    }

    /**
     * @return a Token representing a variable name.
     */
    public Token getToken() {
        return token;
    }

    /**
     * @return A List of nested Token names representing the Context name to access the variable from (separated by '.').
     */
    public List<Token> getContext() {
        return nestedContexts;
    }
}
