package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.lexing.Token;

import java.util.Collections;
import java.util.List;

/**
 * A simple Node representing an attempt to assign a value to a variable.
 */
public class VarAssignNode extends VarNode {
    private final Token type;
    private final Token token;
    private final List<Token> nestedContexts;
    private final Node valueNode;

    /**
     * @param type - A Token representing the variable's type.
     * @param token - The Token containing the variable name to be accessed.
     * @param nestedContexts - A List of nested Token names representing the Context name to access the variable from (separated by '.').
     * @param valueNode - A Node containing the value to be assigned.
     * @param mods - A ModifierList containing all of the modifiers for the variable.
     */
    public VarAssignNode(Token type, Token token, List<Token> nestedContexts, Node valueNode, ModifierList mods) {
        super(nestedContexts.size() == 0 ? token.getPosStart() : nestedContexts.get(0).getPosStart(), token.getPosEnd(), mods, (String) token.getValue());
        this.nestedContexts = nestedContexts;
        this.type = type;
        this.token = token;
        this.valueNode = valueNode;
    }
    /**
     * Overloaded constructor without Contexts.
     * @param type - A Token representing the variable's type.
     * @param token - The Token containing the variable name to be accessed.
     * @param valueNode - A Node containing the value to be assigned.
     * @param mods - A ModifierList containing all of the modifiers for the variable.
     */
    public VarAssignNode(Token type, Token token, Node valueNode, ModifierList mods) {
        this(type, token, Collections.emptyList(), valueNode, mods);
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
    public Token getType() {
        return type;
    }

    /**
     * @return a Node containing the value to set to the variable.
     */
    public Node getValueNode() {
        return valueNode;
    }

    public List<Token> getNestedContexts() {
        return nestedContexts;
    }
}
