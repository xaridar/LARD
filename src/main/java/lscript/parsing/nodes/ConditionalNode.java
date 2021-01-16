package lscript.parsing.nodes;

import lscript.Tuple;

import java.util.List;

/**
 * A simple Node representing a conditional statement.
 */
public class ConditionalNode extends Node {

    private final List<Tuple<Tuple<Node, Node>, Boolean>> cases;
    private final Tuple<Node, Boolean> elseCase;
    private final boolean braces;

    public ConditionalNode(List<Tuple<Tuple<Node, Node>, Boolean>> cases, Tuple<Node, Boolean> elseCase, boolean braces) {
        super(cases.get(0).getLeft().getLeft().getPosStart(), (elseCase == null ? cases.get(cases.size() - 1).getLeft().getRight() : elseCase.getLeft()).getPosEnd());
        this.cases = cases;
        this.elseCase = elseCase;
        this.braces = braces;
    }
    public ConditionalNode(List<Tuple<Tuple<Node, Node>, Boolean>> cases, Tuple<Node, Boolean> elseCase) {
        this(cases, elseCase, false);
    }

    /**
     * @return a Tuple containing the Node to call in the else case and a Boolean determining whether the else case should return a Value (in the case of ?: operators). Can be null.
     */
    public Tuple<Node, Boolean> getElseCase() {
        return elseCase;
    }

    /**
     * @return a list of Tuples containing the Node to call in each case and a Boolean determining whether the case should return a Value (in the case of ?: operators).
     */
    public List<Tuple<Tuple<Node, Node>, Boolean>> getCases() {
        return cases;
    }

    /**
     * Override of the requiresSemicolon() method in Node.
     * @return a boolean determining whether this statement must end with a semicolon; if it is surrounded by braces, it does not, but otherwise, it does.
     */
    @Override
    public boolean requiresSemicolon() {
        return !braces;
    }
}
