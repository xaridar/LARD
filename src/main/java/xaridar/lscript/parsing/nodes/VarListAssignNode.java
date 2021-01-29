package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.lexing.Token;

import java.util.List;

/**
 * A Node representing an attempt to assign a value to a list of variables.
 */
public class VarListAssignNode extends Node {

    private final List<Tuple<Token, Token>> vars;
    private final Node valueNode;
    private final boolean allSameType;

    /**
     * @param vars - A list of tuples of variable types and names to be set.
     * @param valueNode - A Node containing the value(s) to be assigned.
     * @param allSameType - a boolean designating whether all of the variables to be assigned in this node are of the same type.
     */
    public VarListAssignNode(List<Tuple<Token, Token>> vars, Node valueNode, boolean allSameType) {
        super(vars.get(0).getLeft().getPosStart(), valueNode.getPosEnd());
        this.vars = vars;
        this.valueNode = valueNode;
        this.allSameType = allSameType;
    }

    public List<Tuple<Token, Token>> getVars() {
        return vars;
    }

    public Node getValueNode() {
        return valueNode;
    }

    public boolean isAllSameType() {
        return allSameType;
    }
}
