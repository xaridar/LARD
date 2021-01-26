package lscript.parsing.nodes;

import lscript.Tuple;
import lscript.lexing.Token;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
