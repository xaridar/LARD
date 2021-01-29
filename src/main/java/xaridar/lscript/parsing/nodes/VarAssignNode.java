package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.lexing.Token;

/**
 * A simple Node representing an attempt to assign a value to a variable.
 */
public class VarAssignNode extends VarNode {
    private final Token type;
    private final Token token;
    private final Node valueNode;

    /**
     * @param type - A Token representing the variable's type.
     * @param token - The Token containing the variable name to be accessed.
     * @param valueNode - A Node containing the value to be assigned.
     * @param mods - A ModifierList containing all of the modifiers for the variable.
     */
    public VarAssignNode(Token type, Token token, Node valueNode, ModifierList mods) {
        super(token.getPosStart(), token.getPosEnd(), mods, (String) token.getValue());
        this.type = type;
        this.token = token;
        this.valueNode = valueNode;
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
}
