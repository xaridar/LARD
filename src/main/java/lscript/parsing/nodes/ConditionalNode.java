package lscript.parsing.nodes;

import lscript.Tuple;

import java.util.List;

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

    public Tuple<Node, Boolean> getElseCase() {
        return elseCase;
    }

    public List<Tuple<Tuple<Node, Node>, Boolean>> getCases() {
        return cases;
    }

    @Override
    public boolean requiresSemicolon() {
        return !braces;
    }
}
