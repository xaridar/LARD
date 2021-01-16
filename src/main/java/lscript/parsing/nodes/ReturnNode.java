package lscript.parsing.nodes;

import lscript.lexing.Position;

import java.util.List;

public class ReturnNode extends Node {
    private final List<Node> nodesToCall;

    public ReturnNode(List<Node> nodesToCall, Position posStart, Position posEnd) {
        super(posStart, posEnd);
        this.nodesToCall = nodesToCall;
    }

    public List<Node> getNodesToCall() {
        return nodesToCall;
    }
}
